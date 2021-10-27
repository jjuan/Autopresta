package mx.saccsa.autopresta

import grails.rest.RestfulController

class PortafoliosController extends RestfulController<Portafolios> {
    PortafoliosController() {
        super(Portafolios)
    }

    def index() {
        respond(Portafolios.list().collect(
                {
                    [
                            cvePortafolio: it.cvePortafolio,
                            descripcion  : it.descripcion,
                            fecha        : it.fecha,
                            mercados     : it.mercados.descripcion,
                    ]
                }
        ))
    }
}
