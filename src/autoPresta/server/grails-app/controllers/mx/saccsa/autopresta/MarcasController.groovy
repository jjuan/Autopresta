package mx.saccsa.autopresta

import grails.rest.RestfulController

class MarcasController extends RestfulController<Marcas> {
    MarcasController() {
        super(Marcas)
    }

    def index() {
        respond(Marcas.list().collect(
                {
                    [
                            id: it.id,
                            slug: it.slug,
                            nombre: it.nombre
                    ]
                }
        ))
    }
}
