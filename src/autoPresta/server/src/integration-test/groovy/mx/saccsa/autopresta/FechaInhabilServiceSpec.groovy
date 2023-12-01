package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class FechaInhabilServiceSpec extends Specification {

    FechaInhabilService fechaInhabilService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new FechaInhabil(...).save(flush: true, failOnError: true)
        //new FechaInhabil(...).save(flush: true, failOnError: true)
        //FechaInhabil fechaInhabil = new FechaInhabil(...).save(flush: true, failOnError: true)
        //new FechaInhabil(...).save(flush: true, failOnError: true)
        //new FechaInhabil(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //fechaInhabil.id
    }

    void "test get"() {
        setupData()

        expect:
        fechaInhabilService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<FechaInhabil> fechaInhabilList = fechaInhabilService.list(max: 2, offset: 2)

        then:
        fechaInhabilList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        fechaInhabilService.count() == 5
    }

    void "test delete"() {
        Long fechaInhabilId = setupData()

        expect:
        fechaInhabilService.count() == 5

        when:
        fechaInhabilService.delete(fechaInhabilId)
        sessionFactory.currentSession.flush()

        then:
        fechaInhabilService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        FechaInhabil fechaInhabil = new FechaInhabil()
        fechaInhabilService.save(fechaInhabil)

        then:
        fechaInhabil.id != null
    }
}
