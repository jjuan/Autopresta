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
class ContratoController extends RestfulController<Contrato>{
    ContratoController() {
        super(Contrato)
    }
    def contratoService

    @Transactional
    def save() {
        params
        request.JSON
        Boolean principal = true
        def portafolio = Portafolios.findByCvePortafolio(1) as Portafolios
        def fecha = portafolio.fecha
        Contrato contrato = resource.newInstance()
        bindData contrato, request.JSON
        contrato.validate()
        contrato.save(flush: true, failOnError: true)

        TipoContrato tcontrato = TipoContrato.findById(request.JSON.tipoContrato as long)

        for (dir in request.JSON.direccion) {
            Direccion direccion = new Direccion()
            direccion.contrato = contrato
            direccion.dirTrabajo = dir.dirTrabajo
            direccion.dirAdicional = dir.dirAdicional
            direccion.direccionPrincipal = dir.direccionPrincipal
            direccion.exterior = dir.exterior
            direccion.interior = dir.interior
            direccion.cp = new Long(dir.cp as String)
            direccion.colonia = dir.colonia
            direccion.municipio = dir.municipio
            direccion.entidad = dir.entidad
            direccion.principal = principal
            direccion.save(flush: true, failOnError: true)
            principal = false
        }

        for (Integer i = 1; i < tcontrato.duracion; i++){
            fecha = contratoService.calcularFechaPago(i)
            ContratoDetalle contratoDetalle = new ContratoDetalle()
            contratoDetalle.contrato = contrato
            contratoDetalle.parcialidad = i
            contratoDetalle.fecha = fecha
            contratoDetalle.interes = contrato.costoMensualInteres
            contratoDetalle.monitoreo = contrato.costoMensualMonitoreo
            contratoDetalle.gps = contrato.costoMensualGPS
            contratoDetalle.capital = 0
            contratoDetalle.subtotal = ( contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS)
            contratoDetalle.iva = (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS) * 0.16
            contratoDetalle.saldoFinal = contrato.montoRequerido
            contratoDetalle.save(flush: true, failOnError: true)
        }


        fecha = contratoService.calcularFechaPago(12)
        ContratoDetalle contratoDetalle = new ContratoDetalle()
        contratoDetalle.contrato = contrato
        contratoDetalle.parcialidad = tcontrato.duracion
        contratoDetalle.fecha = fecha
        contratoDetalle.interes = contrato.costoMensualInteres
        contratoDetalle.monitoreo = contrato.costoMensualMonitoreo
        contratoDetalle.gps = contrato.costoMensualGPS
        contratoDetalle.capital = contrato.montoRequerido
        contratoDetalle.subtotal = ( contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS + contrato.montoRequerido)
        contratoDetalle.iva = (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS) * 0.16
        contratoDetalle.saldoFinal = 0
        contratoDetalle.save(flush: true, failOnError: true)

        respond(contrato)
    }
}
