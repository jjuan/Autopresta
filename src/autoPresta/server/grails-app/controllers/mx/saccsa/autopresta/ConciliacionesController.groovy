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
        respond(conciliacionesService.cargarMovimientos(cuenta, params.cargoAbono as Boolean, fechaInicio, fechaFin))
    }

    def cargarParcialidades() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        respond(conciliacionesService.cargarParcialidades(fechaInicio, fechaFin))
    }

    def statusConciliaciones() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cuenta = params.cuenta ? params.cuenta as String : Parametros.getValorByParametro('CuentaAP')

        def conciliadas = params.cargoAbono?conciliacionesService.cargarMovimientos(cuenta, params.cargoAbono as Boolean, fechaInicio, fechaFin, true):conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, true)
        def noConciliadas = params.cargoAbono?conciliacionesService.cargarMovimientos(cuenta, params.cargoAbono as Boolean, fechaInicio, fechaFin, false):conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, true)

        respond(conciliadas: conciliadas.size(), pendientes: noConciliadas.size(), total: conciliadas.size() + noConciliadas.size() )
    }

    Portafolios portafolio() {
        return Portafolios.findByCvePortafolio(Parametros.getValorByParametro('Portafolio') as Integer)
    }


}
