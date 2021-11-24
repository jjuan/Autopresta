package mx.saccsa.autopresta

import grails.gorm.services.Service

@Service(IdentificacionesOficiales)
interface IdentificacionesOficialesService {

    IdentificacionesOficiales get(Serializable id)

    List<IdentificacionesOficiales> list(Map args)

    Long count()

    void delete(Serializable id)

    IdentificacionesOficiales save(IdentificacionesOficiales identificacionesOficiales)

}