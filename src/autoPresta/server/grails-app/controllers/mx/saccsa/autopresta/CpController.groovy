package mx.saccsa.autopresta

import grails.gorm.transactions.ReadOnly
import grails.rest.RestfulController

@ReadOnly
class CpController extends RestfulController<Cp>{
    CpController(){
        super(Cp)
    }
    def index() {
        respond Cp.list().collect{
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
}
