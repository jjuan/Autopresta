package mx.saccsa.autopresta

import mx.saccsa.common.Parametros
import mx.saccsa.restfull.CatalogoController

import java.text.SimpleDateFormat

class ConciliacionesController extends CatalogoController<Conciliaciones> {
    ConciliacionesController() {
        super(Conciliaciones)
    }

    SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')
    def conciliacionesService

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
        if (params.conciliados){
            respond(conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, new Boolean(params.conciliados)))
        } else {
            respond(conciliacionesService.cargarParcialidades(fechaInicio, fechaFin))
        }
    }

    def statusConciliacionesMovimientos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cuenta = params.cuenta ? params.cuenta as String : Parametros.getValorByParametro('CuentaAP')

        def conciliadas = conciliacionesService.cargarMovimientos(cuenta, params.cargoAbono ==true?true:false, fechaInicio, fechaFin, true)
        def noConciliadas = conciliacionesService.cargarMovimientos(cuenta, params.cargoAbono ==true?true:false, fechaInicio, fechaFin, false)

        respond(conciliadas: conciliadas.size(), pendientes: noConciliadas.size(), total: conciliadas.size() + noConciliadas.size() )
    }

    def statusConciliacionesOperaciones() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null

        def conciliadas = conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, true)
        def noConciliadas = conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, false)
//        def contratos =  Contrato.findAllByContratoPruebaOrNumeroContrato(true, '')
//        def conciliadas = ContratoDetalle.findAllByContratoNotInListAndConciliadoAndFechaBetween(contratos, true, fechaInicio, fechaFin)
//        def noConciliadas = ContratoDetalle.findAllByContratoNotInListAndConciliadoAndFechaBetween(contratos, false, fechaInicio, fechaFin)

        respond(conciliadas: conciliadas.size(), pendientes: noConciliadas.size(), total: conciliadas.size() + noConciliadas.size() )
    }

    Portafolios portafolio() {
        return Portafolios.findByCvePortafolio(Parametros.getValorByParametro('Portafolio') as Integer)
    }

    def conciliacionAutomaticaMovimientos(){
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cargoAbono = params.cargoAbono
        def concilio = conciliacionesService.conciliacionAutomaticaMovimientos(cargoAbono, fechaInicio, fechaFin, params.id as Long)
        respond(concilio: concilio)
    }

    def conciliacionAutomaticaContratos(){
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        def concilio = conciliacionesService.conciliacionAutomaticaContratos(fechaInicio, fechaFin, params.id as Long)
        respond(concilio: concilio)
    }

    def conciliacionMovimientos(){
        request.JSON
        params
        Long folioConciliacion = conciliacionesService.crearConciliacion(new BigDecimal(request.JSON.montoParcialidades as String), new BigDecimal(request.JSON.montoMovimientos as String))
        for (detalle in request.JSON.detalles){
            conciliacionesService.crearConciliacionDetalle(folioConciliacion, getMovimiento(detalle.movimiento[0]), detalle.folioOperacion[0].toString(), detalle.tipoOperacion[0], request.JSON.formaConciliacion, getMovimiento(detalle.movimiento[0]), request.JSON.campo)
        }
        respond message: 'Conciliaciòn exitosa'
    }

    LiquidacionBanco getMovimiento(Long id){
        return LiquidacionBanco.findById(id)
    }

}
