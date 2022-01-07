package mx.saccsa.autopresta

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import grails.rest.RestfulController
import org.springframework.dao.DuplicateKeyException

import java.sql.SQLException

@ReadOnly
class CuentasBancariasController extends RestfulController<CuentasBancarias> {
    CuentasBancariasController() { super(CuentasBancarias) }

    def index() {
        respond CuentasBancarias.list().collect {
            [
                    id                : it.id,
                    razonSocial       : it.razonSocial.descLabel,
                    banco             : it.banco.descripcionCorta,
                    alias             : it.alias,
                    cuenta            : it.cuenta,
                    clabe             : it.clabe,
                    fechaDeApertura   : it.fechaDeApertura,
                    estatus           : it.estatus,
                    fechaDeCancelacion: it.fechaDeCancelacion,
                    moneda            : it.moneda.descripcion,
            ]
        }
    }

    @Transactional
    def cargarCuenta(String id) {
        respond CuentasBancarias.findById(id as long);
    }

    @Transactional
    def save() {
        params
        request.JSON

        def instance = resource.newInstance()
        bindData instance, request.JSON
        instance.validate()

        if (instance.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond instance.errors, view: 'create' // STATUS CODE 422
            return
        }

        try {
            instance.save insert: true, flush: true, failOnError: true
            CuentaSaldo cuentaSaldo = new CuentaSaldo()
            cuentaSaldo.cuenta = instance
            cuentaSaldo.divisa = instance.moneda
            cuentaSaldo.banco = instance.banco
            cuentaSaldo.cierre = false
            cuentaSaldo.saldo = 0
            cuentaSaldo.saldoInicial = 0
            cuentaSaldo.save(flush: true, failOnError: true)
        } catch (DuplicateKeyException ignored) {
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond errors: [[message: "Registro duplicado"]]
            return
        } catch (SQLException sql) {
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond errors: [[message: "Error en base de datos: " + sql.message]]
            return
        } catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond errors: [[message: "Excepcion generada al momento de guardar el registro. " + ex.message]]
            return
        }
        respond(instance)
    }
}
