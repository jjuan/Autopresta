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
            [
                    folio     : it.id,
                    cuenta    : it.cuenta,
                    fecha     : it.fecha,
                    referencia: it.referencia,
                    monto     : it.monto,
                    estatus   : it.conciliado ? 'Conciliado' : 'Pendiente',
                    clase     : getFolioAndClass(it).clase
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
            [
                    folio      : it.id,
                    contrato   : reporteService.contratoFolio(it.contrato),
                    parcialidad: it.parcialidad,
                    fecha      : it.fecha,
                    monto      : it.subtotal + it.iva,
                    estatus    : it.conciliado ? 'Conciliado' : 'Pendiente',
                    clase      : getFolioAndClass(it).clase
            ]
        })
        return lista
    }

    def conciliacionAutomaticaMovimientos(String cargoAbono, Date fechaInicio, Date fechaFin, Long id) {
        LiquidacionBanco moviento = LiquidacionBanco.findById(id)
        Boolean concilio = false
        def pagos = []
        if (cargoAbono == 'false') {
            def parcialidades = ContratoDetalle.findAllByContratoNotInListAndConciliadoAndFechaBetween(contratosExcluidos(), false, fechaInicio, fechaFin)
            for (parcialidad in parcialidades) {
                BigDecimal monto = parcialidad.iva + parcialidad.subtotal
                if (monto == moviento.monto) {
                    pagos.push(parcialidad)
                }
            }
            if (pagos.size() == 1) {
                ContratoDetalle parcialidad = ContratoDetalle.findById(pagos[0].id as Long)
                def conciliacion = crearConciliacion(parcialidad.iva + parcialidad.subtotal, moviento.monto)
                def operacion = getFolioAndClass(parcialidad)
                crearConciliacionDetalle(conciliacion, moviento, operacion.folio, operacion.clase)
                concilio = true
            }
            return concilio
        }
    }

    def conciliacionAutomaticaContratos(Date fechaInicio, Date fechaFin, Long id) {
        ContratoDetalle contratoDetalle = ContratoDetalle.findById(id)
        Boolean concilio = false
        def pagos = []
        def movimientos = LiquidacionBanco.findAllByCargoAbonoAndMontoAndConciliadoAndFechaBetween(false, contratoDetalle.subtotal + contratoDetalle.iva, false, fechaInicio, fechaFin)
        for (movimiento in movimientos) {
            if (contratoDetalle.subtotal + contratoDetalle.iva == movimiento.monto) {
                pagos.push(movimiento)
            }
        }
        if (pagos.size() == 1) {
            LiquidacionBanco mv = LiquidacionBanco.findById(pagos[0].id as Long)
            def conciliacion = crearConciliacion(contratoDetalle.iva + contratoDetalle.subtotal, mv.monto)
            def operacion = getFolioAndClass(contratoDetalle)
            crearConciliacionDetalle(conciliacion, mv, operacion.folio, operacion.clase)
            concilio = true
        }
        return concilio
    }

    def crearConciliacion(BigDecimal montoXoperaciones, BigDecimal montoXmovimientos) {
        Conciliaciones instance = new Conciliaciones()
        instance.fechaConciliacion = sdf.parse(sdf.format(new Date()))
        instance.montoXoperaciones = montoXoperaciones
        instance.montoXmovimientos = montoXmovimientos
        def diferencia = calculoDiferencia(montoXoperaciones, montoXmovimientos)
        instance.diferencia = diferencia.diferencia as BigDecimal
        instance.descripcionDiferencia = diferencia.descripcionDiferencia
        instance.usuario = getUsuario()
        instance.save(flush: true, failOnError: true)
        return instance.id
    }

    def crearConciliacionDetalle(Long conciliaciones, LiquidacionBanco movimiento, String folioOperacion, String tipoOperacion) {
        ConciliacionesDetalles instance = new ConciliacionesDetalles()
        instance.conciliaciones = Conciliaciones.findById(conciliaciones)
        instance.movimiento = movimiento
        instance.folioOperacion = folioOperacion
        instance.tipoOperacion = tipoOperacion
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

    def getUsuario() {
        return springSecurityService.getCurrentUser() as Usuario
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
}
