package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class IdentificacionesOficialesServiceSpec extends Specification {

    IdentificacionesOficialesService identificacionesOficialesService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new IdentificacionesOficiales(...).save(flush: true, failOnError: true)
        //new IdentificacionesOficiales(...).save(flush: true, failOnError: true)
        //IdentificacionesOficiales identificacionesOficiales = new IdentificacionesOficiales(...).save(flush: true, failOnError: true)
        //new IdentificacionesOficiales(...).save(flush: true, failOnError: true)
        //new IdentificacionesOficiales(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //identificacionesOficiales.id
    }

    void "test get"() {
        setupData()

        expect:
        identificacionesOficialesService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<IdentificacionesOficiales> identificacionesOficialesList = identificacionesOficialesService.list(max: 2, offset: 2)

        then:
        identificacionesOficialesList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        identificacionesOficialesService.count() == 5
    }

    void "test delete"() {
        Long identificacionesOficialesId = setupData()

        expect:
        identificacionesOficialesService.count() == 5

        when:
        identificacionesOficialesService.delete(identificacionesOficialesId)
        sessionFactory.currentSession.flush()

        then:
        identificacionesOficialesService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        IdentificacionesOficiales identificacionesOficiales = new IdentificacionesOficiales()
        identificacionesOficialesService.save(identificacionesOficiales)

        then:
        identificacionesOficiales.id != null
    }
}
