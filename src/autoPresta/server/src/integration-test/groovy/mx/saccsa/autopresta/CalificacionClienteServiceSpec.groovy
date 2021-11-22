package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class CalificacionClienteServiceSpec extends Specification {

    CalificacionClienteService calificacionClienteService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new CalificacionCliente(...).save(flush: true, failOnError: true)
        //new CalificacionCliente(...).save(flush: true, failOnError: true)
        //CalificacionCliente calificacionCliente = new CalificacionCliente(...).save(flush: true, failOnError: true)
        //new CalificacionCliente(...).save(flush: true, failOnError: true)
        //new CalificacionCliente(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //calificacionCliente.id
    }

    void "test get"() {
        setupData()

        expect:
        calificacionClienteService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<CalificacionCliente> calificacionClienteList = calificacionClienteService.list(max: 2, offset: 2)

        then:
        calificacionClienteList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        calificacionClienteService.count() == 5
    }

    void "test delete"() {
        Long calificacionClienteId = setupData()

        expect:
        calificacionClienteService.count() == 5

        when:
        calificacionClienteService.delete(calificacionClienteId)
        sessionFactory.currentSession.flush()

        then:
        calificacionClienteService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        CalificacionCliente calificacionCliente = new CalificacionCliente()
        calificacionClienteService.save(calificacionCliente)

        then:
        calificacionCliente.id != null
    }
}
