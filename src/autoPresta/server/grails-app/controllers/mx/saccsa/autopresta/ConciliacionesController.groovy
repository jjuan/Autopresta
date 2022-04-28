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
        def fechaInicio = sdf.parse(params.fechaInicio)
        def fechaFin = sdf.parse(params.fechaFin)

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
//        if (request.JSON.montoParcialidades && request.JSON.montoMovimientos && request.JSON.porMovimientos) {
        Long folioConciliacion = conciliacionesService.crearConciliacion(new BigDecimal(request.JSON.montoParcialidades as String), new BigDecimal(request.JSON.montoMovimientos as String), request.JSON.porMovimientos, request.JSON.conciliacionParcial)
        for (detalle in request.JSON.detalles) {
            conciliacionesService.crearConciliacionDetalle(folioConciliacion, getMovimiento(detalle.movimiento[0]), detalle.folioOperacion[0].toString(), detalle.tipoOperacion[0], request.JSON.formaConciliacion, getMovimiento(detalle.movimiento[0]), request.JSON.campo)
        }
//        }

        respond message: 'Conciliaciòn exitosa'
    }

    @Transactional
    def conciliacionMovimientosParcial() {
        request.JSON
        params
//        if (request.JSON.montoParcialidades && request.JSON.montoMovimientos && request.JSON.porMovimientos) {
//        Long folioConciliacion = conciliacionesService.crearConciliacion(new BigDecimal(request.JSON.montoParcialidades as String), new BigDecimal(request.JSON.montoMovimientos as String), request.JSON.porMovimientos, request.JSON.conciliacionParcial)
        for (detalle in request.JSON.detalles) {
            conciliacionesService.crearConciliacionDetalle(params.id as long, getMovimiento(detalle.movimiento[0]), detalle.folioOperacion[0].toString(), detalle.tipoOperacion[0], request.JSON.formaConciliacion, getMovimiento(detalle.movimiento[0]), request.JSON.campo)
        }
        Conciliaciones conciliacion = Conciliaciones.findById(params.id as long)
        conciliacion.conciliacionParcial = request.JSON.conciliacionParcial as Boolean
        conciliacion.save(flush: true)
        //        }

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
            log.debug('folio ' + parcialidad.folio + 'concilio: ' + concilio == false ? 'no' : 'si')
            if (concilio != false) {
                conciliaciones.push(concilio)
            }
        }
        def lista = []
        if (conciliaciones.size() > 0) {
            lista = ConciliacionesDetalles.findAllByConciliacionesInList(Conciliaciones.findAllByIdInList(conciliaciones)).collect({
                [concilio         : true,
//             , parcialidad: [
//                    folio      : it.folioOperacion,
//                    contrato   : reporteService.contratoFolio(ContratoDetalle.findById(it.folioOperacion as Long).contrato),
//                    titular    : conciliacionesService.getTitular(ContratoDetalle.findById(it.folioOperacion as Long)),
//                    parcialidad: ContratoDetalle.findById(it.folioOperacion as Long).parcialidad,
//                    fecha      : ContratoDetalle.findById(it.folioOperacion as Long).fecha,
//                    monto      : ContratoDetalle.findById(it.folioOperacion as Long).subtotal + ContratoDetalle.findById(it.folioOperacion as Long).iva,
//                    estatus    : ContratoDetalle.findById(it.folioOperacion as Long).conciliado ? 'Conciliado' : 'Pendiente',
//                    clase      : conciliacionesService.getFolioAndClass(ContratoDetalle.findById(it.folioOperacion as Long)).clase
//            ], movimiento       : [
//                    folio     : it.movimiento.id,
//                    cuenta    : it.movimiento.cuenta,
//                    fecha     : it.movimiento.fecha,
//                    referencia: it.movimiento.referencia,
//                    monto     : it.movimiento.monto,
//                    estatus   : it.movimiento.conciliado ? 'Conciliado' : 'Pendiente',
//                    clase     : conciliacionesService.getFolioAndClass(it.movimiento).clase
//            ],
                 formaConciliacion: it.formaConciliacion]
            })
        }
        respond lista
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
                    id               : it.id,
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

    @Transactional
    def eliminarConciliacionDetalle() {
        ConciliacionesDetalles detalle = ConciliacionesDetalles.findById(params.id as Long)
        Conciliaciones conciliacion = Conciliaciones.findById(detalle.conciliaciones.id)
        if (detalle.conciliaciones.porMovimiento) {
            ContratoDetalle contratoDetalle = ContratoDetalle.findById(detalle.folioOperacion as long)
            contratoDetalle.conciliado = false
            contratoDetalle.save(flush: true)
            conciliacion.montoXoperaciones = conciliacion.montoXoperaciones - (contratoDetalle.subtotal + contratoDetalle.iva)
        } else {
            LiquidacionBanco liquidacionBanco = LiquidacionBanco.findById(detalle.movimiento.id)
            liquidacionBanco.conciliado = false
            liquidacionBanco.save(flush: true)
            conciliacion.montoXmovimientos = conciliacion.montoXmovimientos - liquidacionBanco.monto
        }
        conciliacion.diferencia = conciliacionesService.calculoDiferencia(conciliacion.montoXoperaciones, conciliacion.montoXmovimientos).diferencia
        conciliacion.descripcionDiferencia = conciliacionesService.calculoDiferencia(conciliacion.montoXoperaciones, conciliacion.montoXmovimientos).descripcionDiferencia
        conciliacion.save(flush: true)
        detalle.delete(flush: true)


        def detalles = ConciliacionesDetalles.findAllByConciliaciones(conciliacion)
        if (detalles.size() == 0) {
            conciliacion.delete(flush: true)
        }
//        }
        respond message: 'ok'
    }

    def getStatus(String nc) {
        respond Contrato.findByNumeroContrato(nc).collect({
            [
                    numeroContrato: it.numeroContrato,
                    titular       : getTitular(it),
                    referencia    : it.referencia != null ? it.referencia : 'No tiene',
                    rfc           : it.rfc != null ? it.rfc : 'No tiene',
                    placas        : it.placas != null ? it.placas : 'No tiene',
                    modelo        : it.modelo != null ? it.modelo.descLabel : 'No tiene',
                    mensualidades : getMensualidades(it)
            ]
        })
    }

    def getMensualidades(Contrato contrato) {
        return ContratoDetalle.findAllByContrato(contrato).collect({
            [
                    id               : it.id,
                    parcialidad      : it.parcialidad,
                    fecha            : it.fecha,
                    total            : it.subtotal + it.iva,
                    conciliado       : it.conciliado,
                    folioConciliacion: it.conciliado == true ? ConciliacionesDetalles.findByFolioOperacion(it.id.toString())!=null?ConciliacionesDetalles.findByFolioOperacion(it.id.toString()).conciliaciones.id : 'Esta conciliado por estatus':'No tiene'
            ]
        })
    }

    def getTitular(Contrato contrato) {
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
}
