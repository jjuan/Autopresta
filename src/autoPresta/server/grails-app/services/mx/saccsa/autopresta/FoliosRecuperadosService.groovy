package mx.saccsa.autopresta

import grails.gorm.services.Service

@Service(FoliosRecuperados)
interface FoliosRecuperadosService {

    FoliosRecuperados get(Serializable id)

    List<FoliosRecuperados> list(Map args)

    Long count()

    void delete(Serializable id)

    FoliosRecuperados save(FoliosRecuperados foliosRecuperados)

}