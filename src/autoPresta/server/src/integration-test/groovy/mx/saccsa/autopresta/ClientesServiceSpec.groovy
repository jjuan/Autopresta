package mx.saccsa.autopresta

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ClientesServiceSpec extends Specification {

    ClientesService clientesService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Clientes(...).save(flush: true, failOnError: true)
        //new Clientes(...).save(flush: true, failOnError: true)
        //Clientes clientes = new Clientes(...).save(flush: true, failOnError: true)
        //new Clientes(...).save(flush: true, failOnError: true)
        //new Clientes(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //clientes.id
    }

    void "test get"() {
        setupData()

        expect:
        clientesService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Clientes> clientesList = clientesService.list(max: 2, offset: 2)

        then:
        clientesList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        clientesService.count() == 5
    }

    void "test delete"() {
        Long clientesId = setupData()

        expect:
        clientesService.count() == 5

        when:
        clientesService.delete(clientesId)
        sessionFactory.currentSession.flush()

        then:
        clientesService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Clientes clientes = new Clientes()
        clientesService.save(clientes)

        then:
        clientes.id != null
    }
}
