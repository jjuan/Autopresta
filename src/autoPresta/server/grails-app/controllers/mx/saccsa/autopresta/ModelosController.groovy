package mx.saccsa.autopresta

import grails.rest.RestfulController

class ModelosController extends RestfulController<Modelos> {
    ModelosController() {
        super(Modelos)
    }

    def index() {
        respond(Modelos.list().collect({
            [
                    id: it.id,
                    marca: it.marca.nombre,
                    nombre: it.nombre,
                    slug: it.slug
            ]
        }))
    }
}
