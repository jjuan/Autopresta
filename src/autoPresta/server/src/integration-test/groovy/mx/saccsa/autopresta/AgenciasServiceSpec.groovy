package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class AgenciasServiceSpec extends Specification {

    AgenciasService agenciasService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Agencias(...).save(flush: true, failOnError: true)
        //new Agencias(...).save(flush: true, failOnError: true)
        //Agencias agencias = new Agencias(...).save(flush: true, failOnError: true)
        //new Agencias(...).save(flush: true, failOnError: true)
        //new Agencias(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //agencias.id
    }

    void "test get"() {
        setupData()

        expect:
        agenciasService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Agencias> agenciasList = agenciasService.list(max: 2, offset: 2)

        then:
        agenciasList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        agenciasService.count() == 5
    }

    void "test delete"() {
        Long agenciasId = setupData()

        expect:
        agenciasService.count() == 5

        when:
        agenciasService.delete(agenciasId)
        sessionFactory.currentSession.flush()

        then:
        agenciasService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Agencias agencias = new Agencias()
        agenciasService.save(agencias)

        then:
        agencias.id != null
    }
}
