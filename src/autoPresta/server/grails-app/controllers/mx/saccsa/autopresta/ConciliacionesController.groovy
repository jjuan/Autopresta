package mx.saccsa.autopresta

import mx.saccsa.restfull.CatalogoController

import java.text.SimpleDateFormat

class ConciliacionesController extends CatalogoController<Conciliaciones>{
    ConciliacionesController() {
        super(Conciliaciones)
    }

    SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')

    def cargarMovimientos(){
        def fechaInicio = params.fechaInicio?sdf.parse(sdf.format(params.fechaInicio)):null
        def fechaFin = params.fechaFin?sdf.parse(sdf.format(params.fechaFin)):null


    }
}
