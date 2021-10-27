package mx.saccsa.autopresta

import grails.gorm.transactions.ReadOnly
import grails.rest.RestfulController

@ReadOnly
class C_RegimenFiscalController extends RestfulController<C_RegimenFiscal>{
    C_RegimenFiscalController() {super(C_RegimenFiscal)}

}
