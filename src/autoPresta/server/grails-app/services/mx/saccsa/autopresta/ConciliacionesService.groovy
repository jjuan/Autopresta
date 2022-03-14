package mx.saccsa.autopresta

import grails.gorm.transactions.Transactional
import mx.saccsa.security.Usuario

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
             estatus   : it.conciliado ? 'Conciliado' : 'Pendiente',
             clase     : getFolioAndClass(it).clase]
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
             parcialidad: it.parcialidad,
             fecha      : it.fecha,
             monto      : it.subtotal + it.iva,
             estatus    : it.conciliado ? 'Conciliado' : 'Pendiente',
             clase      : getFolioAndClass(it).clase]
        })
        return lista
    }

    def conciliacionAutomaticaMovimientos(String cargoAbono, Date fechaInicio, Date fechaFin, Long id, Boolean... confirmaConciliacion) {
        LiquidacionBanco moviento = LiquidacionBanco.findById(id)
        String formaConciliacion = ''
        String campo = ''
        HojaConciliacion hojaConciliacion = verificarHoja(moviento)
        Boolean concilio = false
        def pagos = []
        if (cargoAbono == 'false') {
            def parcialidades = ContratoDetalle.findAllByContratoNotInListAndConciliadoAndFechaBetween(contratosExcluidos() != null ? contratosExcluidos() : [], false, fechaInicio, fechaFin)
            for (parcialidad in parcialidades) {
                Boolean encontro = false
                BigDecimal monto = parcialidad.iva + parcialidad.subtotal
                if (monto == moviento.monto) {
                    String cliente = parcialidad.contrato.razonesSociales != null ? parcialidad.contrato.razonesSociales.razonSocial : nombreCliente(parcialidad.contrato)
                    String referencia = parcialidad.contrato.referencia
                    String contrato = parcialidad.contrato.numeroContrato
                    String rfc = parcialidad.contrato.razonesSociales != null ? parcialidad.contrato.razonesSociales.rfc : parcialidad.contrato.rfc
                    String placas = parcialidad.contrato.placas

                    if (moviento.referencia.contains(cliente) && !encontro) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla1 = cliente
                        campo = cliente
                        formaConciliacion = 'Nombre del cliente'
                        encontro = true
                    }

                    if (referencia != null && moviento.referencia.contains(referencia) && !encontro) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla2 = referencia
                        campo = referencia
                        formaConciliacion = 'Referencia bancaria'
                    }

                    if (moviento.referencia.contains(contrato) && !encontro) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla3 = contrato
                        campo = contrato
                        encontro = true
                        formaConciliacion = 'Número de contrato'
                    }

                    if (hojaConciliacion.regla4 != null && moviento.referencia.contains(hojaConciliacion.regla4) && !encontro) {
                        pagos.push(parcialidad)
                        campo = contrato
                        encontro = true
                        formaConciliacion = 'Número de cuenta'
                    }

                    if (moviento.referencia.contains(rfc) && !encontro) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla5 = rfc
                        campo = rfc
                        encontro = true
                        formaConciliacion = 'RFC del cliente'
                    }

                    if (moviento.referencia.contains(placas) && !encontro) {
                        pagos.push(parcialidad)
                        hojaConciliacion.regla6 = placas
                        campo = placas
                        encontro = true
                        formaConciliacion = 'Placas del vehículo del cliente'
                    }

                    if (hojaConciliacion.regla8 != null && moviento.referencia.contains(hojaConciliacion.regla8) && !encontro) {
                        pagos.push(parcialidad)
                        campo = hojaConciliacion.regla8
                        encontro = true
                        formaConciliacion = 'Cajero'
                    }

                }
            }
            if (pagos.size() == 1) {
                ContratoDetalle parcialidad = ContratoDetalle.findById(pagos[0].id as Long)
                def operacion = getFolioAndClass(parcialidad)
                if (confirmaConciliacion && (confirmaConciliacion[0] as Boolean)) {
                    def conciliacion = crearConciliacion(parcialidad.iva + parcialidad.subtotal, moviento.monto, true)
                    crearConciliacionDetalle(conciliacion, moviento, operacion.folio, operacion.clase, formaConciliacion, moviento, campo)
                    hojaConciliacion.save(flush: true, failOnError: true)
                }
                concilio = true
                return [concilio    : concilio, parcialidad: [
                        folio      : parcialidad.id,
                        contrato   : reporteService.contratoFolio(parcialidad.contrato),
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
            return false
        }
    }

    def conciliacionAutomaticaContratos(Date fechaInicio, Date fechaFin, Long id, Boolean... confirmaConciliacion) {
        ContratoDetalle contratoDetalle = ContratoDetalle.findById(id)
        String formaConciliacion = ''
        String campo = ''
        HojaConciliacion hojaConciliacion = verificarHoja(contratoDetalle)
        Boolean concilio = false
        def pagos = []
        def movimientos = LiquidacionBanco.findAllByCargoAbonoAndMontoAndConciliadoAndFechaBetween(false, contratoDetalle.subtotal + contratoDetalle.iva, false, fechaInicio, fechaFin)
        for (movimiento in movimientos) {
            Boolean encontro = false
            if (contratoDetalle.subtotal + contratoDetalle.iva == movimiento.monto) {
                String cliente = contratoDetalle.contrato.razonesSociales != null ? contratoDetalle.contrato.razonesSociales.razonSocial : nombreCliente(contratoDetalle.contrato)
                String referencia = contratoDetalle.contrato.referencia
                String contrato = contratoDetalle.contrato.numeroContrato
                String rfc = contratoDetalle.contrato.razonesSociales != null ? contratoDetalle.contrato.razonesSociales.rfc : contratoDetalle.contrato.rfc
                String placas = contratoDetalle.contrato.placas
                if (movimiento.referencia.contains(cliente) && !encontro) {
                    pagos.push(movimiento)
                    hojaConciliacion.regla1 = cliente
                    campo = cliente
                    encontro = true
                    formaConciliacion = 'Nombre del cliente'
                }
                if (referencia != null && movimiento.referencia.contains(referencia) && !encontro) {
                    pagos.push(movimiento)
                    hojaConciliacion.regla2 = referencia
                    campo = referencia
                    encontro = true
                    formaConciliacion = 'Referencia bancaria'
                }

                if (movimiento.referencia.contains(contrato) && !encontro) {
                    pagos.push(movimiento)
                    hojaConciliacion.regla3 = contrato
                    campo = contrato
                    encontro = true
                    formaConciliacion = 'Número de contrato'
                }

                if (hojaConciliacion.regla4 != null && movimiento.referencia.contains(hojaConciliacion.regla4) && !encontro) {
                    pagos.push(movimiento)
                    campo = contrato
                    encontro = true
                    formaConciliacion = 'Número de cuenta'
                }

                if (movimiento.referencia.contains(rfc) && !encontro) {
                    pagos.push(movimiento)
                    hojaConciliacion.regla5 = rfc
                    campo = rfc
                    encontro = true
                    formaConciliacion = 'RFC del cliente'
                }
                if (movimiento.referencia.contains(placas) && !encontro) {
                    pagos.push(movimiento)
                    hojaConciliacion.regla6 = placas
                    campo = placas
                    encontro = true
                    formaConciliacion = 'Placas del vehículo del cliente'
                }

                if (hojaConciliacion.regla8 != null && movimiento.referencia.contains(hojaConciliacion.regla8) && !encontro) {
                    pagos.push(movimiento)
                    campo = hojaConciliacion.regla8
                    encontro = true
                    formaConciliacion = 'Cajero'
                }

            }
        }
        if (pagos.size() == 1) {
            LiquidacionBanco mv = LiquidacionBanco.findById(pagos[0].id as Long)
            def operacion = getFolioAndClass(contratoDetalle)
            if (confirmaConciliacion && (confirmaConciliacion[0] as Boolean)) {
                def conciliacion = crearConciliacion(contratoDetalle.iva + contratoDetalle.subtotal, mv.monto,false)
                crearConciliacionDetalle(conciliacion, mv, operacion.folio, operacion.clase, formaConciliacion, contratoDetalle, campo)
                hojaConciliacion.save(flush: true, failOnError: true)
            }
            concilio = true
            return [concilio    : concilio, parcialidad: [
                    folio      : contratoDetalle.id,
                    contrato   : reporteService.contratoFolio(contratoDetalle.contrato),
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
        return concilio
    }

    def crearConciliacion(BigDecimal montoXoperaciones, BigDecimal montoXmovimientos, Boolean esMovimientos) {
        Conciliaciones instance = new Conciliaciones()
        instance.fechaConciliacion = sdf.parse(sdf.format(new Date()))
        instance.montoXoperaciones = montoXoperaciones
        instance.montoXmovimientos = montoXmovimientos
        instance.porMovimiento = esMovimientos
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
        instance.formaConciliacion = formaConciliacion
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
        return Contrato.findAllByContratoPruebaOrNumeroContrato(true, '')
    }

    String nombreCliente(Contrato contrato) {
        return contrato.nombreLargo != null ? contrato.nombreLargo : contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
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

}
