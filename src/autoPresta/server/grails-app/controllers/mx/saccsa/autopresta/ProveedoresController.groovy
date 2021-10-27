package mx.saccsa.autopresta

import grails.rest.RestfulController

class ProveedoresController extends RestfulController<Proveedores> {
    ProveedoresController() {
        super(Proveedores)
    }

    def index() {
        respond(Proveedores.list().collect(
                {
                    [
                            id: it.id,
                            nombre: it.nombre,
                            rfc: it.rfc,
                            moneda: it.moneda.descLabel,
                            monedaFactura: it.monedaFactura.descLabel,
                            nombreDeContacto: it.nombreDeContacto,
                            correoElectronico: it.correoElectronico,
                            direccion: it.direccion,
                            telefono: it.telefono,
                            estatus: it.estatus,
                            tipo: it.tipo,
                    ]
                }
        ))
    }
}
