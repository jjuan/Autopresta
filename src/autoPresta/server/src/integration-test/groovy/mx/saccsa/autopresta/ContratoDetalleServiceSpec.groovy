package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ContratoDetalleServiceSpec extends Specification {

    ContratoDetalleService contratoDetalleService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new ContratoDetalle(...).save(flush: true, failOnError: true)
        //new ContratoDetalle(...).save(flush: true, failOnError: true)
        //ContratoDetalle contratoDetalle = new ContratoDetalle(...).save(flush: true, failOnError: true)
        //new ContratoDetalle(...).save(flush: true, failOnError: true)
        //new ContratoDetalle(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //contratoDetalle.id
    }

    void "test get"() {
        setupData()

        expect:
        contratoDetalleService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<ContratoDetalle> contratoDetalleList = contratoDetalleService.list(max: 2, offset: 2)

        then:
        contratoDetalleList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        contratoDetalleService.count() == 5
    }

    void "test delete"() {
        Long contratoDetalleId = setupData()

        expect:
        contratoDetalleService.count() == 5

        when:
        contratoDetalleService.delete(contratoDetalleId)
        sessionFactory.currentSession.flush()

        then:
        contratoDetalleService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        ContratoDetalle contratoDetalle = new ContratoDetalle()
        contratoDetalleService.save(contratoDetalle)

        then:
        contratoDetalle.id != null
    }
}
