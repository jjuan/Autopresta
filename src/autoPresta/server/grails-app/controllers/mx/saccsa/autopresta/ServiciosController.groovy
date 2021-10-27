package mx.saccsa.autopresta

import grails.rest.RestfulController

class ServiciosController extends RestfulController<Servicios> {
    ServiciosController() {
        super(Servicios)
    }

    def index() {
        respond(Servicios.list().collect(
                {
                    [
                        id: it?.id,
                        gps1: it?.gps1,
                        proveedor1: it?.proveedor1,
                        gps2: it?.gps2,
                        proveedor2: it?.proveedor2,
                        gps3: it?.gps3,
                        proveedor3: it?.proveedor3,
                    ]
                }
        ))
    }
}
