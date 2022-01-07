package mx.saccsa.autopresta

import grails.rest.RestfulController

class BancoController extends RestfulController<Banco>{
    BancoController() { super(Banco)}

    def index() {
        respond Banco.list().collect{
            [
                    id: it.id,
                    descripcionCorta: it.descripcionCorta,
                    descripcionLarga: it.descripcionLarga,
                    direccion_clc: it.direccion_clc,
                    direccion_con: it.direccion_con,
                    direccion_dis: it.direccion_dis,
                    pais: it.pais,
            ]
        }
    }
}
