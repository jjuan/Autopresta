package mx.saccsa.autopresta

import grails.gorm.services.Service

@Service(FechaInhabil)
interface FechaInhabilService {

    FechaInhabil get(Serializable id)

    List<FechaInhabil> list(Map args)

    Long count()

    void delete(Serializable id)

    FechaInhabil save(FechaInhabil fechaInhabil)

}