package mx.saccsa.autopresta

import grails.gorm.transactions.ReadOnly
import grails.rest.RestfulController

@ReadOnly
class CpController extends RestfulController<Cp>{
    CpController(){
        super(Cp)
    }
    def index() {
        respond Cp.findAllByCodigoPostal(params.cp as  String).collect{
            [
                    id: it.id,
                    codigoPostal: it.codigoPostal,
                    asentamiento: it.asentamiento,
                    ciudad: it.ciudad,
                    municipio: it.municipio,
                    estado: it.estado,
            ]
        }.unique()
    }

    def cargarDatos(String cp) {
        respond(Cp.findAllByCodigoPostal(cp).collect({
            [
                    codigoPostal: it.codigoPostal,
                    ciudad: it.ciudad,
                    municipio: it.municipio,
                    estado: it.estado,
//                    asentamiento: it.asentamiento,
            ]
        }).unique()
        )
    }
}
