package mx.saccsa.autopresta

import grails.gorm.services.Service

@Service(ContratosCarga)
interface ContratosCargaService {

    ContratosCarga get(Serializable id)

    List<ContratosCarga> list(Map args)

    Long count()

    void delete(Serializable id)

    ContratosCarga save(ContratosCarga contratosCarga)

}