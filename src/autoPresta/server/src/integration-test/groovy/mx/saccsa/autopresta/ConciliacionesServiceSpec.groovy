package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ConciliacionesServiceSpec extends Specification {

    ConciliacionesService conciliacionesService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Conciliaciones(...).save(flush: true, failOnError: true)
        //new Conciliaciones(...).save(flush: true, failOnError: true)
        //Conciliaciones conciliaciones = new Conciliaciones(...).save(flush: true, failOnError: true)
        //new Conciliaciones(...).save(flush: true, failOnError: true)
        //new Conciliaciones(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //conciliaciones.id
    }

    void "test get"() {
        setupData()

        expect:
        conciliacionesService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Conciliaciones> conciliacionesList = conciliacionesService.list(max: 2, offset: 2)

        then:
        conciliacionesList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        conciliacionesService.count() == 5
    }

    void "test delete"() {
        Long conciliacionesId = setupData()

        expect:
        conciliacionesService.count() == 5

        when:
        conciliacionesService.delete(conciliacionesId)
        sessionFactory.currentSession.flush()

        then:
        conciliacionesService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Conciliaciones conciliaciones = new Conciliaciones()
        conciliacionesService.save(conciliaciones)

        then:
        conciliaciones.id != null
    }
}
