package mx.saccsa.autopresta

import grails.gorm.services.Service

@Service(CargadeContratos)
interface CargadeContratosService {

    CargadeContratos get(Serializable id)

    List<CargadeContratos> list(Map args)

    Long count()

    void delete(Serializable id)

    CargadeContratos save(CargadeContratos cargadeContratos)

}