package mx.saccsa.autopresta

import grails.gorm.transactions.Transactional
import mx.saccsa.security.Usuario
import org.w3c.dom.CDATASection

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
        if (conciliados) {
            lista = ContratoDetalle.findAllByContratoNotInListAndConciliadoAndFechaBetween(contratosExcluidos(), conciliados[0] as Boolean, fechaInicio, fechaFin)
        } else {
            lista = ContratoDetalle.findAllByContratoNotInListAndFechaBetween(contratosExcluidos(), fechaInicio, fechaFin)
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
//             porMovimiento: ConciliacionesDetalles.findByFolioOperacion(it.id.toString())!=null?Conciliaciones.findById(ConciliacionesDetalles.findByFolioOperacion(it.id.toString()).id).porMovimiento:'N/A'
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

    def conciliacionAutomaticaMovimientos(String cargoAbono, Date fechaInicio, Date fechaFin, Long id, Boolean... confirmaConciliacion) {
        LiquidacionBanco moviento = LiquidacionBanco.findById(id)
        String formaConciliacion = ''
        String campo = ''
        Boolean concilio = false
        def pagos = []
        if (cargoAbono == 'false') {
            def parcialidades = ContratoDetalle.findAllByContratoNotInListAndConciliadoAndFechaLessThanEquals(contratosExcluidos() != null ? contratosExcluidos() : [], false, fechaFin)

            for (parcialidad in parcialidades) {
                HojaConciliacion hojaConciliacion = verificarHoja(parcialidad.contrato)
                Boolean encontro = false
                BigDecimal monto = parcialidad.iva + parcialidad.subtotal
//                if (monto == moviento.monto) {
                String cliente = parcialidad.contrato.razonesSociales != null ? parcialidad.contrato.razonesSociales.razonSocial : nombreCliente(parcialidad.contrato)
                String referencia = parcialidad.contrato.referencia
                String contrato = parcialidad.contrato.numeroContrato
                String rfc = parcialidad.contrato.razonesSociales != null ? parcialidad.contrato.razonesSociales.rfc : parcialidad.contrato.rfc
                String placas = parcialidad.contrato.placas

                if (moviento.referencia.contains(cliente) && !encontro && moviento.referencia.contains('0941763 ') == false) {
                    pagos.push(parcialidad)
                    hojaConciliacion.regla1 = cliente
                    campo = cliente
                    formaConciliacion = 'Nombre del cliente'
                    hojaConciliacion.save(flush: true, failOnError: true)
                    log.error "movimiento: " + moviento.id
                    return concilia(parcialidad, moviento, formaConciliacion, campo)
                }

                if (referencia != null && moviento.referencia.contains(referencia) && !encontro && moviento.referencia.contains('0941763 ') == false) {
                    pagos.push(parcialidad)
                    hojaConciliacion.regla2 = referencia
                    campo = referencia
                    formaConciliacion = 'Referencia bancaria'
                    hojaConciliacion.save(flush: true, failOnError: true)
                    log.error "movimiento: " + moviento.id
                    return concilia(parcialidad, moviento, formaConciliacion, campo)
                }

                if (moviento.referencia.contains(contrato) && !encontro && moviento.referencia.contains('0941763 ') == false) {
                    pagos.push(parcialidad)
                    hojaConciliacion.regla3 = contrato
                    campo = contrato
                    encontro = true
                    formaConciliacion = 'Número de contrato'
                    hojaConciliacion.save(flush: true, failOnError: true)
                    log.error "movimiento: " + moviento.id
                    return concilia(parcialidad, moviento, formaConciliacion, campo)
                }

                if (hojaConciliacion.regla4 != null && moviento.referencia.contains(hojaConciliacion.regla4) && !encontro && moviento.referencia.contains('0941763 ') == false) {
                    pagos.push(parcialidad)
                    campo = contrato
                    encontro = true
                    formaConciliacion = 'Número de cuenta'
                    hojaConciliacion.save(flush: true, failOnError: true)
                    log.error "movimiento: " + moviento.id
                    return concilia(parcialidad, moviento, formaConciliacion, campo)
                }
                if (rfc != null) {
                    if (moviento.referencia.contains(rfc) && !encontro && moviento.referencia.contains('0941763 ') == false) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla5 = rfc
                        campo = rfc
                        encontro = true
                        formaConciliacion = 'RFC del cliente'
                        hojaConciliacion.save(flush: true, failOnError: true)
                        log.error "movimiento: " + moviento.id
                        return concilia(parcialidad, moviento, formaConciliacion, campo)
                    }
                }

                if (placas != null) {
                    if (moviento.referencia.contains(placas) && !encontro) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla6 = placas
                        campo = placas
                        encontro = true
                        formaConciliacion = 'Placas del vehículo del cliente'
                        hojaConciliacion.save(flush: true, failOnError: true)
                        log.error "movimiento: " + moviento.id
                        return concilia(parcialidad, moviento, formaConciliacion, campo)
                    }
                }

                if (hojaConciliacion.regla8 != null && moviento.referencia.contains(hojaConciliacion.regla8) && !encontro && moviento.referencia.contains('0941763 ') == false) {
                    pagos.push(parcialidad)
                    campo = hojaConciliacion.regla8
                    encontro = true
                    formaConciliacion = 'Cajero'
                    hojaConciliacion.save(flush: true, failOnError: true)
                    log.error "movimiento: " + moviento.id
                    return concilia(parcialidad, moviento, formaConciliacion, campo)
                }

                hojaConciliacion.save(flush: true, failOnError: true)
//                }
            }
//            log.error 'va el ' + moviento.id.toString() + ' coincidencias: ' + pagos.size().toString()
//            if (pagos.size() >= 1) {
//                ContratoDetalle parcialidad = ContratoDetalle.findById(pagos[0].id as Long)
//                def operacion = getFolioAndClass(parcialidad)
//                if (confirmaConciliacion && (confirmaConciliacion[0] as Boolean)) {
//                    def conciliacion = crearConciliacion(parcialidad.iva + parcialidad.subtotal, moviento.monto, true, true)
//                    crearConciliacionDetalle(conciliacion, moviento, operacion.folio, operacion.clase, formaConciliacion, moviento, campo)
//                }
//                concilio = true
//                return [concilio    : concilio, parcialidad: [
//                        folio      : parcialidad.id,
//                        contrato   : reporteService.contratoFolio(parcialidad.contrato),
//                        titular    : getTitular(parcialidad),
//                        parcialidad: parcialidad.parcialidad,
//                        fecha      : parcialidad.fecha,
//                        monto      : parcialidad.subtotal + parcialidad.iva,
//                        estatus    : parcialidad.conciliado ? 'Conciliado' : 'Pendiente',
//                        clase      : getFolioAndClass(parcialidad).clase
//                ], movimiento       : [
//                        folio     : moviento.id,
//                        cuenta    : moviento.cuenta,
//                        fecha     : moviento.fecha,
//                        referencia: moviento.referencia,
//                        monto     : moviento.monto,
//                        estatus   : moviento.conciliado ? 'Conciliado' : 'Pendiente',
//                        clase     : getFolioAndClass(moviento).clase
//                ], formaConciliacion: formaConciliacion]
//            }
            return false
        }
    }


    def concilia(ContratoDetalle parcialidad, LiquidacionBanco moviento, String formaConciliacion, String campo) {
//        ContratoDetalle parcialidad = ContratoDetalle.findById(pagos[0].id as Long)
        def operacion = getFolioAndClass(parcialidad)
//        if (confirmaConciliacion && (confirmaConciliacion[0] as Boolean)) {
        def conciliacion = crearConciliacion(parcialidad.iva + parcialidad.subtotal, moviento.monto, false, true)
        crearConciliacionDetalle(conciliacion, moviento, operacion.folio, operacion.clase, formaConciliacion, moviento, campo)
//        }
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
        def movimientos = LiquidacionBanco.findAllByCargoAbonoAndMontoAndConciliadoAndFechaBetween(false, contratoDetalle.subtotal + contratoDetalle.iva, false, fechaInicial, fechaFinal)
        for (movimiento in movimientos) {
            Boolean encontro = false
//            if (contratoDetalle.subtotal + contratoDetalle.iva == movimiento.monto) {
            String cliente = contratoDetalle.contrato.razonesSociales != null ? contratoDetalle.contrato.razonesSociales.razonSocial : nombreCliente(contratoDetalle.contrato)
            String referencia = contratoDetalle.contrato.referencia
            String contrato = contratoDetalle.contrato.numeroContrato
            String rfc = contratoDetalle.contrato.razonesSociales != null ? contratoDetalle.contrato.razonesSociales.rfc : contratoDetalle.contrato.rfc
            String placas = contratoDetalle.contrato.placas
            if (movimiento.referencia.contains(cliente) && !encontro && movimiento.referencia.contains('0941763 ') == false) {
                pagos.push(movimiento)
                hojaConciliacion.regla1 = cliente
                campo = cliente
                encontro = true
                formaConciliacion = 'Nombre del cliente'
            }
            if (referencia != null && movimiento.referencia.contains(referencia) && !encontro && movimiento.referencia.contains('0941763 ') == false) {
                pagos.push(movimiento)
                hojaConciliacion.regla2 = referencia
                campo = referencia
                encontro = true
                formaConciliacion = 'Referencia bancaria'
            }

            if (movimiento.referencia.contains(contrato) && !encontro && movimiento.referencia.contains('0941763 ') == false) {
                pagos.push(movimiento)
                hojaConciliacion.regla3 = contrato
                campo = contrato
                encontro = true
                formaConciliacion = 'Número de contrato'
            }

            if (hojaConciliacion.regla4 != null && movimiento.referencia.contains(hojaConciliacion.regla4 && movimiento.referencia.contains('0941763 ') == false) && !encontro) {
                pagos.push(movimiento)
                campo = contrato
                encontro = true
                formaConciliacion = 'Número de cuenta'
            }

            if (rfc != null && movimiento.referencia.contains(rfc) && !encontro && movimiento.referencia.contains('0941763 ') == false) {
                pagos.push(movimiento)
                hojaConciliacion.regla5 = rfc
                campo = rfc
                encontro = true
                formaConciliacion = 'RFC del cliente'
            }
            if (placas != null && movimiento.referencia.contains(placas) && !encontro) {
                pagos.push(movimiento)
                hojaConciliacion.regla6 = placas
                campo = placas
                encontro = true
                formaConciliacion = 'Placas del vehículo del cliente'
            }

            if (hojaConciliacion.regla8 != null && movimiento.referencia.contains(hojaConciliacion.regla8) && !encontro && movimiento.referencia.contains('0941763 ') == false) {
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
//            return [concilio    : concilio, parcialidad: [
//                    folio      : contratoDetalle.id,
//                    contrato   : reporteService.contratoFolio(contratoDetalle.contrato),
//                    titular    : getTitular(contratoDetalle),
//                    parcialidad: contratoDetalle.parcialidad,
//                    fecha      : contratoDetalle.fecha,
//                    monto      : contratoDetalle.subtotal + contratoDetalle.iva,
//                    estatus    : contratoDetalle.conciliado ? 'Conciliado' : 'Pendiente',
//                    clase      : getFolioAndClass(contratoDetalle).clase
//            ], movimiento       : [
//                    folio     : mv.id,
//                    cuenta    : mv.cuenta,
//                    fecha     : mv.fecha,
//                    referencia: mv.referencia,
//                    monto     : mv.monto,
//                    estatus   : mv.conciliado ? 'Conciliado' : 'Pendiente',
//                    clase     : getFolioAndClass(mv).clase
//            ], formaConciliacion: formaConciliacion]
        } else if (pagos.size() > 1) {
            def montoXmovimientos = 0
            def conciliacion = crearConciliacion(contratoDetalle.iva + contratoDetalle.subtotal, 0 as BigDecimal, false, true)
            for (pago in pagos) {
                if (monto > 0) {
                    LiquidacionBanco mv = LiquidacionBanco.findById(pago.id as Long)
                    monto = monto - mv.monto
                    montoXmovimientos = mv.monto
                    def operacion = getFolioAndClass(contratoDetalle)
                    if (confirmaConciliacion && (confirmaConciliacion[0] as Boolean)) {
                        crearConciliacionDetalle(conciliacion, mv, operacion.folio, operacion.clase, formaConciliacion, contratoDetalle, campo)
                        hojaConciliacion.save(flush: true, failOnError: true)
                    }
                    concilio = true
                    conciliaciones.push(conciliacion)

                }
            }

            Conciliaciones instance = Conciliaciones.findById(conciliacion)
            instance.montoXmovimientos = montoXmovimientos
            def diferencia = calculoDiferencia(instance.montoXoperaciones, montoXmovimientos as BigDecimal)
            instance.diferencia = diferencia.diferencia as BigDecimal
            instance.descripcionDiferencia = diferencia.descripcionDiferencia
            instance.save(flush: true, failOnError: true)

            conciliaciones.push(conciliacion)

//            return [concilio: concilio, detalles: getDetalles(instance), formaConciliacion: formaConciliacion]
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

    def crearConciliacionDetalle(Long conciliaciones, LiquidacionBanco movimiento, String folioOperacion, String tipoOperacion, String formaConciliacion, Object objeto, String campo) {
        ConciliacionesDetalles instance = new ConciliacionesDetalles()
        instance.conciliaciones = Conciliaciones.findById(conciliaciones)
        instance.movimiento = movimiento
        instance.folioOperacion = folioOperacion
        instance.tipoOperacion = tipoOperacion
        instance.fecha = new Date()
        instance.usuario = getUsuario()
        HojaConciliacion hojaConciliacion = verificarHoja(objeto)
        switch (formaConciliacion) {
            case "regla1":
                hojaConciliacion.regla1 = campo
                break;
            case "regla2":
                hojaConciliacion.regla2 = campo
                break;
            case "regla3":
                hojaConciliacion.regla3 = campo
                break;
            case "regla4":
                hojaConciliacion.regla4 = campo
                break;
            case "regla5":
                hojaConciliacion.regla5 = campo
                break;
            case "regla6":
                hojaConciliacion.regla6 = campo
                break;
            case "regla7":
                hojaConciliacion.regla7 = campo
                break;
            case "regla8":
                hojaConciliacion.regla8 = campo
                break;
        }

        hojaConciliacion.save(flush: true, failOnError: true)
        instance.hojaConciliacion = hojaConciliacion
        instance.formaConciliacion = getForma(formaConciliacion)
        instance.save(flush: true, failOnError: true)
        actualizaMovimento(movimiento.id)
        actualizarOperacion(folioOperacion, tipoOperacion, movimiento)
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

    def actualizaMovimento(Long id) {
        LiquidacionBanco movimiento = LiquidacionBanco.findById(id)
        movimiento.conciliado = true
        movimiento.save(flush: true, failOnError: true)
    }

    def actualizarOperacion(String folio, String tipoOperacion, LiquidacionBanco movimiento) {
        String groupId = 'mx.saccsa.autopresta'
        Class c = Class.forName(groupId + "." + tipoOperacion.capitalize())
        def data = c.findById(folio as Long)
        data.conciliado = true
        if (tipoOperacion == 'ContratoDetalle') {
            data.fechaPago = movimiento.fecha
            data.estatus = 'C'
        }
        data.save(flush: true, failOnError: true)
    }

    def contratosExcluidos() {
        return Contrato.findAllByContratoPruebaOrNumeroContratoOrTipoFolio(true, '', 'P')
    }

    String nombreCliente(Contrato contrato) {
//        return contrato.nombreLargo != null ? contrato.nombreLargo : contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
        return contrato.nombreLargo != null ? contrato.nombreLargo : contrato.nombres + ' ' + contrato.primerApellido
    }

    HojaConciliacion verificarHoja(Object objeto) {
        def registro = getFolioAndClass(objeto)
        HojaConciliacion hojaConciliacion = HojaConciliacion.findByFolioAndClase(registro.folio, registro.clase)
        if (hojaConciliacion == null) {
            hojaConciliacion = new HojaConciliacion()
            hojaConciliacion.folio = registro.folio
            hojaConciliacion.clase = registro.clase
            hojaConciliacion.save(flush: true, failOnError: true)
        }
        return hojaConciliacion
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
//        if (cd.conciliaciones.porMovimiento && cd.conciliaciones.conciliacionParcial && movimiento || !cd.conciliaciones.porMovimiento && cd.conciliaciones.conciliacionParcial && !movimiento) {
        if (cd.conciliaciones.conciliacionParcial) {
            status = 'Conciliacion Parcial'
        } else {
            status = 'Conciliado'
        }
        return status
    }
}
