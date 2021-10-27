package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ContratoServiceSpec extends Specification {

    ContratoService contratoService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Contrato(...).save(flush: true, failOnError: true)
        //new Contrato(...).save(flush: true, failOnError: true)
        //Contrato contrato = new Contrato(...).save(flush: true, failOnError: true)
        //new Contrato(...).save(flush: true, failOnError: true)
        //new Contrato(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //contrato.id
    }

    void "test get"() {
        setupData()

        expect:
        contratoService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Contrato> contratoList = contratoService.list(max: 2, offset: 2)

        then:
        contratoList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        contratoService.count() == 5
    }

    void "test delete"() {
        Long contratoId = setupData()

        expect:
        contratoService.count() == 5

        when:
        contratoService.delete(contratoId)
        sessionFactory.currentSession.flush()

        then:
        contratoService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Contrato contrato = new Contrato()
        contratoService.save(contrato)

        then:
        contrato.id != null
    }
}
