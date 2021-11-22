package mx.saccsa.autopresta

import grails.gorm.services.Service

@Service(CalificacionCliente)
interface CalificacionClienteService {

    CalificacionCliente get(Serializable id)

    List<CalificacionCliente> list(Map args)

    Long count()

    void delete(Serializable id)

    CalificacionCliente save(CalificacionCliente calificacionCliente)

}