package mx.saccsa.autopresta

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import mx.saccsa.security.Usuario
import org.apache.tools.ant.taskdefs.Length
import org.w3c.dom.CDATASection

import java.sql.Array
import java.text.SimpleDateFormat

//
@Transactional
class ConciliacionesService {
    def reporteService
    def springSecurityService
    SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')

    def cargarMovimientos(String cuenta, Boolean cargoAbono, Date fechaInicio, Date fechaFin, Boolean... conciliados) {
        def lista
        if (conciliados) {
            lista = LiquidacionBanco.findAllByCuentaAndCargoAbonoAndConciliadoAndFechaBetween(cuenta, cargoAbono, conciliados[0] as Boolean, fechaInicio, fechaFin)
        } else {
            lista = LiquidacionBanco.findAllByCuentaAndCargoAbonoAndFechaBetween(cuenta, cargoAbono, fechaInicio, fechaFin)
        }
        lista = lista.collect({
            [folio     : it.id,
             cuenta    : it.cuenta,
             fecha     : it.fecha,
             referencia: it.referencia,
             monto     : it.monto,
             estatus   : ConciliacionesDetalles.findByMovimiento(it) != null ? getStatus(ConciliacionesDetalles.findByMovimiento(it), true) : 'Pendiente',
             clase     : getFolioAndClass(it).clase,
             raiz      : getRaiz(it.id.toString(), getFolioAndClass(it).clase).id,
             claseRaiz : getRaiz(it.id.toString(), getFolioAndClass(it).clase).clase,
//             porMovimiento: ConciliacionesDetalles.findByMovimiento(it)!=null?Conciliaciones.findById(ConciliacionesDetalles.findByMovimiento(it).id).porMovimiento:'N/A'
            ]
        })
        return lista
    }

    def cargarParcialidades(Date fechaInicio, Date fechaFin, Boolean... conciliados) {
        def lista
        def estatusContrato = ['Inpago (vendido)', 'Liquidado anticipado', 'Liquidado a tiempo', 'Fraude']
        if (conciliados) {
            lista = ContratoDetalle.findAllByIdInListAndConciliado(
                    Contrato.findAllByFechaCorteBetweenAndEstatusContratoNotInListAndTipoFolioNotEqualAndContratoPruebaAndEstatusNotEqualAndNumeroContratoNotEqual(
                            fechaInicio, fechaFin, estatusContrato, "P", false, "C", "")*.mensualidadActual, conciliados[0] as Boolean)
        } else {
            lista = ContratoDetalle.findAllByIdInList(
                    Contrato.findAllByFechaCorteBetweenAndEstatusContratoNotInListAndTipoFolioNotEqualAndContratoPruebaAndEstatusNotEqualAndNumeroContratoNotEqual(
                            fechaInicio, fechaFin, estatusContrato, "P", false, "C", "")*.mensualidadActual)
        }
        lista = lista.collect({
            [folio      : it.id,
             contrato   : reporteService.contratoFolio(it.contrato),
             titular    : getTitular(it),
             parcialidad: it.parcialidad,
             fecha      : it.fecha,
             monto      : it.subtotal + it.iva,
             estatus    : ConciliacionesDetalles.findByFolioOperacion(it.id.toString()) != null ? getStatus(ConciliacionesDetalles.findByFolioOperacion(it.id.toString()), false) : 'Pendiente',
             clase      : getFolioAndClass(it).clase,
             raiz       : getRaiz(it.id.toString(), getFolioAndClass(it).clase).id,
             claseRaiz  : getRaiz(it.id.toString(), getFolioAndClass(it).clase).clase,
            ]
        })
        return lista
    }

    def getRaiz(String f, String c) {
        ConciliacionesDetalles conciliacionesDetalles
        String id = 'N/A'
        String clase = 'N/A'

        if (c == 'LiquidacionBanco') {
            conciliacionesDetalles = ConciliacionesDetalles.findByMovimiento(LiquidacionBanco.findById(f as long))
        } else {
            conciliacionesDetalles = ConciliacionesDetalles.findByFolioOperacion(f)
        }
        if (conciliacionesDetalles != null) {
            if (conciliacionesDetalles.conciliaciones.porMovimiento == true) {
                id = conciliacionesDetalles.movimiento.id.toString()
                clase = 'LiquidacionBanco'
            } else {
                id = conciliacionesDetalles.folioOperacion
                clase = conciliacionesDetalles.tipoOperacion
            }
        }
        return [id: id, clase: clase]
    }

    def conciliacionAutomaticaMovimientos(String cargoAbono, Date fechaInicio, Date fechaFin, Long id, ArrayList idMensualidades, Boolean... confirmaConciliacion) {
        LiquidacionBanco moviento = LiquidacionBanco.findById(id)
        String formaConciliacion = ''
        String campo = ''
        Boolean concilio = false
        def pagos = []
        def estatusContrato = ['Inpago (vendido)', 'Liquidado anticipado', 'Liquidado a tiempo', 'Fraude']
        if (cargoAbono == 'false') {

            def excluidos = Contrato.findAllByContratoPruebaOrTipoFolioOrEstatusOrNumeroContrato(true, "P", "C", "")
            def contratos = Contrato.findAllByIdNotInList(excluidos*.id).sort({ it.numeroContrato.length() }).reverse()
            for (contrato1 in contratos) {
                def parcialidades = ContratoDetalle.findAllByIdInListAndConciliado(
                        Contrato.findAllByFechaCorteLessThanEqualsAndEstatusContratoNotInListAndTipoFolioNotEqualAndContratoPruebaAndEstatusNotEqualAndNumeroContratoNotEqual(
                                fechaFin, estatusContrato, "P", false, "C", "")*.mensualidadActual, false)
                for (parcialidad in parcialidades) {
                    String ap = '0941763'
                    HojaConciliacion hojaConciliacion = verificarHoja(parcialidad.contrato)
                    Boolean encontro = false
                    BigDecimal monto = parcialidad.iva + parcialidad.subtotal
                    String contrato = parcialidad.contrato.numeroContrato != null ? parcialidad.contrato.numeroContrato.toLowerCase() : null
                    String referencia = parcialidad.contrato.referencia != null ? parcialidad.contrato.referencia.toLowerCase() : null
                    if (moviento.referencia.toLowerCase().contains(contrato) && !encontro && ap.contains(contrato) == false) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla3 = contrato
                        campo = contrato
                        encontro = true
                        formaConciliacion = 'Número de contrato'
                        hojaConciliacion.save(flush: true, failOnError: true)
                        return concilia(parcialidad, moviento, formaConciliacion, campo)
                    }
                    if (referencia != null && moviento.referencia.toLowerCase().contains(referencia) && !encontro && ap.contains(contrato) == false) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla2 = referencia
                        campo = referencia
                        formaConciliacion = 'Referencia bancaria'
                        hojaConciliacion.save(flush: true, failOnError: true)
                        return concilia(parcialidad, moviento, formaConciliacion, campo)
                    }
                    hojaConciliacion.save(flush: true, failOnError: true)
                }
            }
            return false
        }
    }


    def concilia(ContratoDetalle parcialidad, LiquidacionBanco moviento, String formaConciliacion, String campo) {
        def operacion = getFolioAndClass(parcialidad)
        def conciliacion = crearConciliacion(parcialidad.iva + parcialidad.subtotal, moviento.monto, false, true)
        crearConciliacionDetalle(conciliacion, moviento, operacion.folio, operacion.clase, formaConciliacion, moviento, campo)
        return [concilio    : true, parcialidad: [
                folio      : parcialidad.id,
                contrato   : reporteService.contratoFolio(parcialidad.contrato),
                titular    : getTitular(parcialidad),
                parcialidad: parcialidad.parcialidad,
                fecha      : parcialidad.fecha,
                monto      : parcialidad.subtotal + parcialidad.iva,
                estatus    : parcialidad.conciliado ? 'Conciliado' : 'Pendiente',
                clase      : getFolioAndClass(parcialidad).clase
        ], movimiento       : [
                folio     : moviento.id,
                cuenta    : moviento.cuenta,
                fecha     : moviento.fecha,
                referencia: moviento.referencia,
                monto     : moviento.monto,
                estatus   : moviento.conciliado ? 'Conciliado' : 'Pendiente',
                clase     : getFolioAndClass(moviento).clase
        ], formaConciliacion: formaConciliacion]
    }

    def conciliacionAutomaticaContratos(Date fechaInicio, Date fechaFin, Long id, Boolean... confirmaConciliacion) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fechaInicio)
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        Date fechaInicial = sdf.parse(sdf.format(calendar.getTime()));

        calendar.setTime(fechaFin)
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        Date fechaFinal = sdf.parse(sdf.format(calendar.getTime()));


        ContratoDetalle contratoDetalle = ContratoDetalle.findById(id)
        BigDecimal monto = contratoDetalle.subtotal + contratoDetalle.iva

        def conciliaciones = []
        String formaConciliacion = ''
        String campo = ''
        HojaConciliacion hojaConciliacion = verificarHoja(contratoDetalle.contrato)
        Boolean concilio = false
        def pagos = []
        def movimientos = LiquidacionBanco.findAllByCargoAbonoAndConciliadoAndFechaBetween(false, false, fechaInicial, fechaFinal)
        for (movimiento in movimientos) {
            Boolean encontro = false
            String cliente = contratoDetalle.contrato.razonesSociales != null ? contratoDetalle.contrato.razonesSociales.razonSocial.toLowerCase() : nombreCliente(contratoDetalle.contrato)
            String referencia = contratoDetalle.contrato.referencia != null ? contratoDetalle.contrato.referencia.toLowerCase() : null
            String rfc = contratoDetalle.contrato.razonesSociales != null ? contratoDetalle.contrato.razonesSociales.rfc.toLowerCase() : contratoDetalle.contrato.rfc != null ? contratoDetalle.contrato.rfc.toLowerCase() : null
            String placas = contratoDetalle.contrato.placas != null ? contratoDetalle.contrato.placas.toLowerCase() : null
            if (movimiento.referencia.toLowerCase().contains(cliente) && !encontro) {
                pagos.push(movimiento)
                hojaConciliacion.regla1 = cliente
                campo = cliente
                encontro = true
                formaConciliacion = 'Nombre del cliente'
            }
            if (referencia != null && movimiento.referencia.toLowerCase().contains(referencia) && !encontro) {
                pagos.push(movimiento)
                hojaConciliacion.regla2 = referencia
                campo = referencia
                encontro = true
                formaConciliacion = 'Referencia bancaria'
            }

            if (rfc != null && movimiento.referencia.toLowerCase().contains(rfc) && !encontro) {
                pagos.push(movimiento)
                hojaConciliacion.regla5 = rfc
                campo = rfc
                encontro = true
                formaConciliacion = 'RFC del cliente'
            }
            if (placas != null && movimiento.referencia.toLowerCase().contains(placas) && !encontro) {
                pagos.push(movimiento)
                hojaConciliacion.regla6 = placas
                campo = placas
                encontro = true
                formaConciliacion = 'Placas del vehículo del cliente'
            }

            if (hojaConciliacion.regla8 != null && movimiento.referencia.toLowerCase().contains(hojaConciliacion.regla8) && !encontro) {
                pagos.push(movimiento)
                campo = hojaConciliacion.regla8
                encontro = true
                formaConciliacion = 'Cajero'
            }

//            }
        }
        if (pagos.size() == 1) {
            LiquidacionBanco mv = LiquidacionBanco.findById(pagos[0].id as Long)
            def operacion = getFolioAndClass(contratoDetalle)
            if (confirmaConciliacion && (confirmaConciliacion[0] as Boolean)) {
                def conciliacion = crearConciliacion(contratoDetalle.iva + contratoDetalle.subtotal, mv.monto, false, true)
                crearConciliacionDetalle(conciliacion, mv, operacion.folio, operacion.clase, formaConciliacion, contratoDetalle, campo)
                hojaConciliacion.save(flush: true, failOnError: true)
                conciliaciones.push(conciliacion)
            }
            concilio = true
            return [concilio    : concilio, parcialidad: [
                    folio      : contratoDetalle.id,
                    contrato   : reporteService.contratoFolio(contratoDetalle.contrato),
                    titular    : getTitular(contratoDetalle),
                    parcialidad: contratoDetalle.parcialidad,
                    fecha      : contratoDetalle.fecha,
                    monto      : contratoDetalle.subtotal + contratoDetalle.iva,
                    estatus    : contratoDetalle.conciliado ? 'Conciliado' : 'Pendiente',
                    clase      : getFolioAndClass(contratoDetalle).clase
            ], movimiento       : [
                    folio     : mv.id,
                    cuenta    : mv.cuenta,
                    fecha     : mv.fecha,
                    referencia: mv.referencia,
                    monto     : mv.monto,
                    estatus   : mv.conciliado ? 'Conciliado' : 'Pendiente',
                    clase     : getFolioAndClass(mv).clase
            ], formaConciliacion: formaConciliacion]
        }
        return concilio == false ? concilio : conciliaciones
    }


    def getDetalles(Conciliaciones conciliaciones) {
        return ConciliacionesDetalles.findAllByConciliaciones(conciliaciones).collect({
            [
                    id               : it.id,
                    movimiento       : LiquidacionBanco.findById(it.movimiento.id).collect({
                        [folio     : it.id,
                         cuenta    : it.cuenta,
                         fecha     : it.fecha,
                         referencia: it.referencia,
                         monto     : it.monto,
                         estatus   : it.conciliado ? 'Conciliado' : 'Pendiente',
                         clase     : getFolioAndClass(it).clase]
                    }),
                    operacion        : ContratoDetalle.findById(it.folioOperacion as Long).collect({
                        [folio      : it.id,
                         contrato   : reporteService.contratoFolio(it.contrato),
                         titular    : getTitular(it),
                         parcialidad: it.parcialidad,
                         fecha      : it.fecha,
                         monto      : it.subtotal + it.iva,
                         estatus    : it.conciliado ? 'Conciliado' : 'Pendiente',
                         clase      : getFolioAndClass(it).clase]
                    }),
                    fecha            : it.fecha,
                    usuario          : it.usuario.descLabel,
                    formaConciliacion: it.formaConciliacion
            ]
        })
    }

    def crearConciliacion(BigDecimal montoXoperaciones, BigDecimal montoXmovimientos, Boolean esMovimientos, Boolean conciliacionParcial) {
        Conciliaciones instance = new Conciliaciones()
        instance.fechaConciliacion = sdf.parse(sdf.format(new Date()))
        instance.montoXoperaciones = montoXoperaciones
        instance.montoXmovimientos = montoXmovimientos
        instance.porMovimiento = esMovimientos
        instance.conciliacionParcial = conciliacionParcial
        def diferencia = calculoDiferencia(montoXoperaciones, montoXmovimientos)
        instance.diferencia = diferencia.diferencia as BigDecimal
        instance.descripcionDiferencia = diferencia.descripcionDiferencia
        instance.save(flush: true, failOnError: true)
        return instance.id
    }

    def crearConciliacionDetalle(Long conciliaciones, LiquidacionBanco movimiento, String folioOperacion, String tipoOperacion, String formaConciliacion, Object objeto, String campo, String... cuenta) {
        ConciliacionesDetalles instance = new ConciliacionesDetalles()
        instance.conciliaciones = Conciliaciones.findById(conciliaciones)
        instance.movimiento = movimiento
        instance.folioOperacion = folioOperacion
        instance.tipoOperacion = tipoOperacion
        instance.fecha = new Date()
        instance.usuario = getUsuario()
        instance.formaConciliacion = getForma(formaConciliacion)
        instance.referenciaBancaria = movimiento.referencia
        if (cuenta) {
            instance.cuenta = cuenta[0] as String
        }
        instance.save(flush: true, failOnError: true)
        actualizaMovimento(movimiento.id)
//        actualizarOperacion(folioOperacion, tipoOperacion, movimiento, Conciliaciones.findById(conciliaciones))
        actualizaPagoMensualidad(movimiento, ContratoDetalle.findById(folioOperacion as Long).contrato)
        return instance.id
    }

    def calculoDiferencia(BigDecimal montoXoperaciones, BigDecimal montoXmovimientos) {
        String descripcion = ''
        def diferencia = montoXmovimientos - montoXoperaciones
        if (diferencia > 0) {
            descripcion = 'Saldo a favor'
        } else if (diferencia == 0) {
            descripcion = 'Liquidado'
        } else if (diferencia < 0) {
            descripcion = 'Saldo en contra'
        }
        return [diferencia: Math.abs(diferencia).toBigDecimal(), descripcionDiferencia: descripcion]
    }

    def getFolioAndClass(Object objeto) {
        String claseFull = objeto.getClass()
        def array = claseFull.split('class mx.saccsa.autopresta.')
        String clase = array[1]
        String folio = objeto.id
        return [clase: clase, folio: folio]
    }

    Usuario getUsuario() {
        return springSecurityService.getCurrentUser()
    }

    @Transactional
    def actualizaMovimento(Long id) {
        LiquidacionBanco movimiento = LiquidacionBanco.findById(id)
        movimiento.conciliado = true
        movimiento.save(flush: true, failOnError: true)
    }

    def actualizarOperacion(String folio, String tipoOperacion, LiquidacionBanco movimiento, Conciliaciones conciliaciones) {
        String groupId = 'mx.saccsa.autopresta'
        Class c = Class.forName(groupId + "." + tipoOperacion.capitalize())
        def data = c.findById(folio as Long)
        data.conciliado = true
        if (tipoOperacion == 'ContratoDetalle') {
            data.fechaPago = movimiento.fecha
            data.estatus = 'C'
            data.montoConciliado = conciliaciones.montoXmovimientos
        }
        data.save(flush: true, failOnError: true)
    }

    def contratosExcluidos() {
        return Contrato.findAllByContratoPruebaOrTipoFolioOrEstatusOrNumeroContratoOrEstatusContratoOrEstatusContratoOrEstatusContratoOrEstatusContrato(true, "P", "C", "", 'Inpago (vendido)', 'Liquidado anticipado', 'Liquidado a tiempo', 'Fraude')
    }

    String nombreCliente(Contrato contrato) {
        return contrato.nombreLargo != null ? contrato.nombreLargo.toLowerCase() : contrato.nombres.toLowerCase() + ' ' + contrato.primerApellido.toLowerCase()
    }

    HojaConciliacion verificarHoja(Contrato contrato) {
        HojaConciliacion hojaConciliacion = HojaConciliacion.findByFolio(contrato)
        if (hojaConciliacion == null) {
            hojaConciliacion = new HojaConciliacion()

            hojaConciliacion.folio = contrato

            hojaConciliacion.regla1 = getTitular2(contrato)

            hojaConciliacion.regla2 = contrato.numeroContrato
            hojaConciliacion.regla3 = contrato.numeroContrato

            if (contrato.rfc != null) {
                hojaConciliacion.regla5 = contrato.rfc
            }
            if (contrato.placas != null) {
                hojaConciliacion.regla6 = contrato.placas
            }

            hojaConciliacion.save(flush: true, failOnError: true)
        }
        return hojaConciliacion
    }

    def getTitular2(Contrato contrato) {
        String titular = ''
        if (contrato.razonesSociales != null) {
            titular = contrato.razonesSociales.razonSocial
        } else {
            if (contrato.nombreLargo != null) {
                titular = contrato.nombreLargo
            } else {

                titular = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
            }
        }
        return titular
    }

    def getTitular(ContratoDetalle contratoDetalle) {
        String titular = ''
        Contrato contrato = Contrato.findById(contratoDetalle.contrato.id)
        if (contrato.razonesSociales != null) {
            titular = contrato.razonesSociales.razonSocial
        } else {
            if (contrato.nombreLargo != null) {
                titular = contrato.nombreLargo
            } else {

                titular = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
            }
        }
        return titular
    }

    @Transactional
    def actualizaPagoMensualidad(LiquidacionBanco lb, Contrato co) {

        ContratoDetalle cd = ContratoDetalle.findByContratoAndParcialidad(co, co.mensualidadActual as Integer)
        def excedente = 0
        def montoMensualidad = cd.subtotal + cd.iva - cd.capital
        if (cd.fechaPago == null) {
            if (lb.fecha <= cd.fecha) {
                cd.fechaPago = lb.fecha
                cd.mensualidad = cd.mensualidad + lb.monto
            } else {
                cd.fechaPago = lb.fecha
                def dias = TimeCategory.minus(lb.fecha , cd.fecha).days
                cd.diasAtraso = dias
                cd.moratorios = (montoMensualidad * 0.10) * dias
                cd.mensualidad = cd.mensualidad + lb.monto
            }
        } else {
            if (cd.mensualidad < (cd.subtotal + cd.iva - cd.capital)) {
                cd.fechaPago = lb.fecha
                def dias = TimeCategory.minus(lb.fecha, cd.fecha).days
                cd.diasAtraso = dias
                cd.moratorios = (montoMensualidad * 0.10) * dias
                cd.mensualidad = cd.mensualidad + lb.monto
            } else {
                cd.mensualidad = cd.mensualidad + lb.monto
            }
        }

        if (cd.mensualidad > montoMensualidad) {
            def monto = cd.mensualidad
            cd.mensualidad = montoMensualidad
            cd.moraoriosPagados = monto - montoMensualidad

            if (cd.moraoriosPagados > cd.moratorios) {
                excedente = cd.moraoriosPagados - cd.moratorios
                cd.moraoriosPagados = cd.moratorios
            }
        }
        cd.save(flush: true, failOnError: true)

        if (cd.mensualidad == montoMensualidad){
            Contrato contrato = Contrato.findById(co.id)
            def sigMen = ContratoDetalle.findByContratoAndParcialidad(contrato, contrato.mensualidadActual + 1 as Integer)
            if (sigMen != null) {
                contrato.mensualidadActual = sigMen.parcialidad
                contrato.fechaCorte = sigMen.fecha
                contrato.save(flush: true, failOnError: true)

//                if (excedente > 0){
////                    sigMen.mensualidad = excedente
////                    sigMen.fechaPago = lb.fecha
//                    sigMen.save(flush: true, failOnError: true)
//                }
            }
        }
    }

    String getForma(String forma) {
        String f = forma
        switch (forma) {
            case 'regla1':
                f = 'Nombre del cliente'
                break
            case 'regla2':
                f = 'Referencia bancaria'
                break
            case 'regla3':
                f = 'Numero de contrato'
                break
            case 'regla4':
                f = 'Número de cuenta'
                break
            case 'regla5':
                f = 'RFC del cliente'
                break
            case 'regla6':
                f = 'Placas del vehículo del cliente'
                break
            case 'regla7':
                f = 'Comprobantes de pago que comparte equipo de cobranza'
                break
            case 'regla8':
                f = 'Cajero'
                break
        }
        return f
    }

    String getStatus(ConciliacionesDetalles cd, Boolean movimiento) {
        String status = 'Pendiente'
        if (cd.conciliaciones.conciliacionParcial) {
            status = 'Conciliacion Parcial'
        } else {
            status = 'Conciliado'
        }
        return status
    }
}
