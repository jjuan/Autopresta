package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class AutomovilesServiceSpec extends Specification {

    AutomovilesService automovilesService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Automoviles(...).save(flush: true, failOnError: true)
        //new Automoviles(...).save(flush: true, failOnError: true)
        //Automoviles automoviles = new Automoviles(...).save(flush: true, failOnError: true)
        //new Automoviles(...).save(flush: true, failOnError: true)
        //new Automoviles(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //automoviles.id
    }

    void "test get"() {
        setupData()

        expect:
        automovilesService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Automoviles> automovilesList = automovilesService.list(max: 2, offset: 2)

        then:
        automovilesList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        automovilesService.count() == 5
    }

    void "test delete"() {
        Long automovilesId = setupData()

        expect:
        automovilesService.count() == 5

        when:
        automovilesService.delete(automovilesId)
        sessionFactory.currentSession.flush()

        then:
        automovilesService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Automoviles automoviles = new Automoviles()
        automovilesService.save(automoviles)

        then:
        automoviles.id != null
    }
}
