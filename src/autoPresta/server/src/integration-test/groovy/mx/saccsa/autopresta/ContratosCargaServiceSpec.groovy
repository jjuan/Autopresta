package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ContratosCargaServiceSpec extends Specification {

    ContratosCargaService contratosCargaService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new ContratosCarga(...).save(flush: true, failOnError: true)
        //new ContratosCarga(...).save(flush: true, failOnError: true)
        //ContratosCarga contratosCarga = new ContratosCarga(...).save(flush: true, failOnError: true)
        //new ContratosCarga(...).save(flush: true, failOnError: true)
        //new ContratosCarga(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //contratosCarga.id
    }

    void "test get"() {
        setupData()

        expect:
        contratosCargaService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<ContratosCarga> contratosCargaList = contratosCargaService.list(max: 2, offset: 2)

        then:
        contratosCargaList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        contratosCargaService.count() == 5
    }

    void "test delete"() {
        Long contratosCargaId = setupData()

        expect:
        contratosCargaService.count() == 5

        when:
        contratosCargaService.delete(contratosCargaId)
        sessionFactory.currentSession.flush()

        then:
        contratosCargaService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        ContratosCarga contratosCarga = new ContratosCarga()
        contratosCargaService.save(contratosCarga)

        then:
        contratosCarga.id != null
    }
}
