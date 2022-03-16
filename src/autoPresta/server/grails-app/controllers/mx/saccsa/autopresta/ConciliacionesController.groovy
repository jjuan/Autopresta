package mx.saccsa.autopresta

import mx.saccsa.common.Parametros
import mx.saccsa.restfull.CatalogoController

import grails.gorm.transactions.Transactional
import java.text.SimpleDateFormat

class ConciliacionesController extends CatalogoController<Conciliaciones> {
    ConciliacionesController() {
        super(Conciliaciones)
    }

    SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')
    def conciliacionesService
    def reporteService

    def cargarMovimientos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cuenta = params.cuenta ? params.cuenta as String : Parametros.getValorByParametro('CuentaAP')
        if (params.conciliados) {
            respond(conciliacionesService.cargarMovimientos(cuenta, new Boolean(params.cargoAbono), fechaInicio, fechaFin, new Boolean(params.conciliados)))
        } else {
            respond(conciliacionesService.cargarMovimientos(cuenta, new Boolean(params.cargoAbono), fechaInicio, fechaFin))
        }
    }

    def cargarParcialidades() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        if (params.conciliados) {
            respond(conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, new Boolean(params.conciliados)))
        } else {
            respond(conciliacionesService.cargarParcialidades(fechaInicio, fechaFin))
        }
    }

    def statusConciliacionesMovimientos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cuenta = params.cuenta ? params.cuenta as String : Parametros.getValorByParametro('CuentaAP')

        def conciliadas = conciliacionesService.cargarMovimientos(cuenta, new Boolean(params.cargoAbono), fechaInicio, fechaFin, true)
        def noConciliadas = conciliacionesService.cargarMovimientos(cuenta, new Boolean(params.cargoAbono), fechaInicio, fechaFin, false)

        respond(conciliadas: conciliadas.size(), pendientes: noConciliadas.size(), total: conciliadas.size() + noConciliadas.size())
    }

    def statusConciliacionesOperaciones() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null

        def conciliadas = conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, true)
        def noConciliadas = conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, false)

        respond(conciliadas: conciliadas.size(), pendientes: noConciliadas.size(), total: conciliadas.size() + noConciliadas.size())
    }

    Portafolios portafolio() {
        return Portafolios.findByCvePortafolio(Parametros.getValorByParametro('Portafolio') as Integer)
    }

    def conciliacionAutomaticaMovimientos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cargoAbono = params.cargoAbono
        def concilio = conciliacionesService.conciliacionAutomaticaMovimientos(cargoAbono, fechaInicio, fechaFin, params.id as Long, true)
        respond(concilio: concilio)
        if (concilio != false) {
            respond concilio
        } else {
            respond(concilio: concilio)
        }
    }

    def conciliacionAutomaticaContratos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        def concilio = conciliacionesService.conciliacionAutomaticaContratos(fechaInicio, fechaFin, params.id as Long, true)
        respond(concilio: concilio)
    }

    def conciliacionMovimientos() {
        request.JSON
        params
        Long folioConciliacion = conciliacionesService.crearConciliacion(new BigDecimal(request.JSON.montoParcialidades as String), new BigDecimal(request.JSON.montoMovimientos as String), request.JSON.porMovimientos)
        for (detalle in request.JSON.detalles) {
            conciliacionesService.crearConciliacionDetalle(folioConciliacion, getMovimiento(detalle.movimiento[0]), detalle.folioOperacion[0].toString(), detalle.tipoOperacion[0], request.JSON.formaConciliacion, getMovimiento(detalle.movimiento[0]), request.JSON.campo)
        }
        respond message: 'Conciliaciòn exitosa'
    }

    LiquidacionBanco getMovimiento(Long id) {
        return LiquidacionBanco.findById(id)
    }

    def conciliacionGeneralMovimentos() {
        def conciliaciones = []
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cuenta = params.cuenta ? params.cuenta as String : Parametros.getValorByParametro('CuentaAP')
        def movimientos = conciliacionesService.cargarMovimientos(cuenta, false, fechaInicio, fechaFin, false)
        for (movimiento in movimientos) {
            def concilio = conciliacionesService.conciliacionAutomaticaMovimientos("false", fechaInicio, fechaFin, movimiento.folio as Long, true)
            if (concilio != false) {
                conciliaciones.push(concilio)
            }
        }
        respond conciliaciones
    }

    def conciliacionGeneralContratos() {
        def conciliaciones = []
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        def parcialidades = conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, false)
        for (parcialidad in parcialidades) {
            def concilio = conciliacionesService.conciliacionAutomaticaContratos(fechaInicio, fechaFin, parcialidad.folio as Long, true)
            if (concilio != false) {
                conciliaciones.push(concilio)
            }
        }
        respond conciliaciones
    }

    def verConciliacion() {
        String folio = params.folio
        String clase = params.clase

        ConciliacionesDetalles conciliacionesDetalles = clase == 'LiquidacionBanco' ? ConciliacionesDetalles.findByMovimiento(LiquidacionBanco.findById(folio as Long)) : ConciliacionesDetalles.findByTipoOperacionAndFolioOperacion(clase, folio)

        def conciliacion = Conciliaciones.findById(conciliacionesDetalles.conciliaciones.id).collect({
            [
                    id                   : it.id,
                    fechaConciliacion    : it.fechaConciliacion,
                    montoXoperaciones    : it.montoXoperaciones,
                    montoXmovimientos    : it.montoXmovimientos,
                    diferencia           : it.diferencia,
                    descripcionDiferencia: it.descripcionDiferencia,
                    conciliacionParcial  : it.conciliacionParcial,
                    detalles             : getDetalles(it),
                    porMovimiento        : it.porMovimiento
            ]
        })
        respond conciliacion
    }

    def getDetalles(Conciliaciones conciliaciones) {
        return ConciliacionesDetalles.findAllByConciliaciones(conciliaciones).collect({
            [
                    movimiento       : LiquidacionBanco.findById(it.movimiento.id).collect({
                        [folio     : it.id,
                         cuenta    : it.cuenta,
                         fecha     : it.fecha,
                         referencia: it.referencia,
                         monto     : it.monto,
                         estatus   : it.conciliado ? 'Conciliado' : 'Pendiente',
                         clase     : conciliacionesService.getFolioAndClass(it).clase]
                    }),
                    operacion        : ContratoDetalle.findById(it.folioOperacion as Long).collect({
                        [folio      : it.id,
                         contrato   : reporteService.contratoFolio(it.contrato),
                         titular    : conciliacionesService.getTitular(it),
                         parcialidad: it.parcialidad,
                         fecha      : it.fecha,
                         monto      : it.subtotal + it.iva,
                         estatus    : it.conciliado ? 'Conciliado' : 'Pendiente',
                         clase      : conciliacionesService.getFolioAndClass(it).clase]
                    }),
                    fecha            : it.fecha,
                    usuario          : it.usuario.descLabel,
                    formaConciliacion: it.formaConciliacion
            ]
        })
    }

    @Transactional
    def eliminarConciliacion() {
        Conciliaciones conciliacion = Conciliaciones.findById(params.id as Long)
        def detalles = ConciliacionesDetalles.findAllByConciliaciones(conciliacion)
        for (detalle in detalles) {
            LiquidacionBanco liquidacionBanco = LiquidacionBanco.findById(detalle.movimiento.id)
            liquidacionBanco.conciliado = false
            liquidacionBanco.save(flush: true)
            ContratoDetalle contratoDetalle = ContratoDetalle.findById(detalle.folioOperacion as long)
            contratoDetalle.conciliado = false
            contratoDetalle.save(flush: true)
            detalle.delete(flush: true)
        }
        conciliacion.delete(flush: true)
        respond message: 'ok'
    }

}
