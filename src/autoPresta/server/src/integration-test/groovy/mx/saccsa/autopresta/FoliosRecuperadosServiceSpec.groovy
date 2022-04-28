package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class FoliosRecuperadosServiceSpec extends Specification {

    FoliosRecuperadosService foliosRecuperadosService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new FoliosRecuperados(...).save(flush: true, failOnError: true)
        //new FoliosRecuperados(...).save(flush: true, failOnError: true)
        //FoliosRecuperados foliosRecuperados = new FoliosRecuperados(...).save(flush: true, failOnError: true)
        //new FoliosRecuperados(...).save(flush: true, failOnError: true)
        //new FoliosRecuperados(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //foliosRecuperados.id
    }

    void "test get"() {
        setupData()

        expect:
        foliosRecuperadosService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<FoliosRecuperados> foliosRecuperadosList = foliosRecuperadosService.list(max: 2, offset: 2)

        then:
        foliosRecuperadosList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        foliosRecuperadosService.count() == 5
    }

    void "test delete"() {
        Long foliosRecuperadosId = setupData()

        expect:
        foliosRecuperadosService.count() == 5

        when:
        foliosRecuperadosService.delete(foliosRecuperadosId)
        sessionFactory.currentSession.flush()

        then:
        foliosRecuperadosService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        FoliosRecuperados foliosRecuperados = new FoliosRecuperados()
        foliosRecuperadosService.save(foliosRecuperados)

        then:
        foliosRecuperados.id != null
    }
}
