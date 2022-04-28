package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class FoliosServiceSpec extends Specification {

    FoliosService foliosService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Folios(...).save(flush: true, failOnError: true)
        //new Folios(...).save(flush: true, failOnError: true)
        //Folios folios = new Folios(...).save(flush: true, failOnError: true)
        //new Folios(...).save(flush: true, failOnError: true)
        //new Folios(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //folios.id
    }

    void "test get"() {
        setupData()

        expect:
        foliosService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Folios> foliosList = foliosService.list(max: 2, offset: 2)

        then:
        foliosList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        foliosService.count() == 5
    }

    void "test delete"() {
        Long foliosId = setupData()

        expect:
        foliosService.count() == 5

        when:
        foliosService.delete(foliosId)
        sessionFactory.currentSession.flush()

        then:
        foliosService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Folios folios = new Folios()
        foliosService.save(folios)

        then:
        folios.id != null
    }
}
