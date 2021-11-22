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
    def folioService

    @Transactional
    def save() {
        params
        request.JSON
        Boolean principal = true
        Contrato contrato = resource.newInstance()
        bindData contrato, request.JSON

        if(contrato.contratoPrueba){
            contrato.numeroContrato = folioService.generaFolio('ContratoPruebas').toString() + 'P'
        } else {
            contrato.numeroContrato = folioService.generaFolio('Contrato').toString()
        }

        contrato.validate()
        contrato.save(flush: true, failOnError: true)

        if (request.JSON.regimenFiscal == 'PM'){
            RazonesSociales instance = new RazonesSociales()
            instance.razonSocial = request.JSON.razonSocialMoral
            instance.rfc = request.JSON.rfcMoral
            instance.telefonoFijo = request.JSON.telefonoFijoMoral
            instance.telefonoCelular = request.JSON.telefonoCelularMoral
            instance.telefonoOficina = request.JSON.telefonoOficinaMoral
            instance.calleDireccionFiscal = request.JSON.calleDireccionFiscalMoral
            instance.numeroExterior = request.JSON.numeroExteriorMoral
            instance.numeroInterior = request.JSON.numeroInteriorMoral
            instance.codigoPostal = request.JSON.codigoPostalMoral
            instance.colonia = request.JSON.coloniaMoral
            instance.entidad = request.JSON.entidadMoral
            instance.municipio = request.JSON.municipioMoral


            contrato.razonesSociales = instance
            contrato.save(flush: true, failOnError: true)
        }
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

        for (Integer i = 1; i <= 12; i++){
            def fecha = contratoService.calcularFechaPago(i)
            ContratoDetalle contratoDetalle = new ContratoDetalle()
            contratoDetalle.contrato = contrato
            contratoDetalle.parcialidad = i
            contratoDetalle.fecha = fecha
            contratoDetalle.interes = contrato.costoMensualInteres
            contratoDetalle.monitoreo = contrato.costoMensualMonitoreo
            contratoDetalle.gps = contrato.costoMensualGPS
            contratoDetalle.capital = i == 12 ? contrato.montoRequerido : 0
            contratoDetalle.subtotal = i == 12 ? ( contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS + contrato.montoRequerido) : ( contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS)
            contratoDetalle.iva = (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS) * 0.16
            contratoDetalle.saldoFinal = i == 12 ? 0 : contrato.montoRequerido
            contratoDetalle.save(flush: true, failOnError: true)
        }
        respond(contrato)
    }

    def folios() {
        Folios contrato = Folios.findByCveTipo('Contrato')
        Folios contratoPrueba = Folios.findByCveTipo('ContratoPruebas')
        String folio = contrato != null ? (contrato.folio + 1).toString(): '1'
        String folioPrueba = contratoPrueba != null ? (contrato.folio + 1).toString() + 'P': '1P'
        respond(folio:  folio,  folioPrueba: folioPrueba)
    }
}
