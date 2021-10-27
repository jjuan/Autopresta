package mx.saccsa.autopresta

import grails.rest.RestfulController

class SucursalesController extends RestfulController<Sucursales> {
    SucursalesController() {
        super(Sucursales)
    }

    def index(){
        respond(Sucursales.list().collect(
                {
                    [
                            id: it.id,
                            descripcion: it.descripcion,
                            region: it.region.descLabel,
                            ciudad: it.ciudad,
                            direccion: it.direccion,
                            colonia: it.colonia,
                            codigoPostal: it.codigoPostal,
                            telefono: it.telefono,
                    ]
                }
        ))
    }
}
