package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class CargadeContratosServiceSpec extends Specification {

    CargadeContratosService cargadeContratosService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new CargadeContratos(...).save(flush: true, failOnError: true)
        //new CargadeContratos(...).save(flush: true, failOnError: true)
        //CargadeContratos cargadeContratos = new CargadeContratos(...).save(flush: true, failOnError: true)
        //new CargadeContratos(...).save(flush: true, failOnError: true)
        //new CargadeContratos(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //cargadeContratos.id
    }

    void "test get"() {
        setupData()

        expect:
        cargadeContratosService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<CargadeContratos> cargadeContratosList = cargadeContratosService.list(max: 2, offset: 2)

        then:
        cargadeContratosList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        cargadeContratosService.count() == 5
    }

    void "test delete"() {
        Long cargadeContratosId = setupData()

        expect:
        cargadeContratosService.count() == 5

        when:
        cargadeContratosService.delete(cargadeContratosId)
        sessionFactory.currentSession.flush()

        then:
        cargadeContratosService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        CargadeContratos cargadeContratos = new CargadeContratos()
        cargadeContratosService.save(cargadeContratos)

        then:
        cargadeContratos.id != null
    }
}
