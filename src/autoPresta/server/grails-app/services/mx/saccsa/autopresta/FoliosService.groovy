package mx.saccsa.autopresta

import grails.gorm.services.Service

@Service(Folios)
interface FoliosService {

    Folios get(Serializable id)

    List<Folios> list(Map args)

    Long count()

    void delete(Serializable id)

    Folios save(Folios folios)

}