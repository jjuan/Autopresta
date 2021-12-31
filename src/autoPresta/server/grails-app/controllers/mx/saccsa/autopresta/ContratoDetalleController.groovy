package mx.saccsa.autopresta

import grails.rest.RestfulController
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class ContratoDetalleController extends RestfulController<ContratoDetalle>{
    ContratoDetalleController() {
        super(ContratoDetalle)
    }

    def detalles(String id) {
        Contrato contrato = Contrato.findById(id as long)
        respond(ContratoDetalle.findAllByContrato(contrato).collect(
                {
                    [
                            id: it.id,
                            contrato: it.contrato.numeroContrato,
                            parcialidad: it.parcialidad,
                            fecha: it.fecha,
                            interes: it.interes,
                            monitoreo: it.monitoreo,
                            gps: it.gps,
                            capital: it.capital,
                            subtotal: it.subtotal,
                            iva: it.iva,
                            total: it.subtotal + it.iva,
                            saldoFinal: it.saldoFinal,
                            fechaPago: it.fechaPago,
                            estatus: it.estatus == 'P'?'Pendiente':'Confirmado'
                    ]
                }
        ))

    }
}
