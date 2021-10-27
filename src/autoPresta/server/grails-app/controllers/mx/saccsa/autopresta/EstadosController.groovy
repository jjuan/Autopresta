package mx.saccsa.autopresta

import grails.gorm.transactions.ReadOnly
import grails.rest.RestfulController

@ReadOnly
class EstadosController extends RestfulController<Estados> {
    EstadosController() {
        super(Estados)
    }

    def index() {
        respond Estados.list().collect{
            [
                    id: it.id,

                    claveEstado: it.claveEstado,
                    numeroPais: it.numeroPais,
                    descripcionCorta: it.descripcionCorta,
                    descripcion: it.descripcion,
            ]
        }
    }
}
