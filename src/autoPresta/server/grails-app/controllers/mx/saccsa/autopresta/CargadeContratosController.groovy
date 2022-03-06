package mx.saccsa.autopresta

import grails.validation.ValidationException
import mx.saccsa.restfull.CatalogoController

import java.text.Bidi

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class CargadeContratosController extends CatalogoController<CargadeContratos>{
    CargadeContratosController() {
        super(CargadeContratos)
    }

    def contratoService

    def capturarContrato() {
        def capturas = CargadeContratos.findAllByCargado(false)
        for (captura in capturas) {
            cargarContrato(captura)
        }
        respond message: 'ok'
    }

    def capturarDireccion() {
        def capturas = CargadeDirecciones.findAllByCargado(false)
        for (captura in capturas) {
            cargarDireccion(captura)
        }
        respond message: 'ok'
    }

    def cargarDireccion(CargadeDirecciones captura) {
        Direccion direccion = new Direccion()
        if (Contrato.findByNumeroContrato(captura.contrato) != null) {
            direccion.contrato = Contrato.findByNumeroContrato(captura.contrato)
            direccion.dirTrabajo = captura.dirTrabajo
            direccion.dirAdicional = captura.dirAdicional
            direccion.direccionPrincipal = captura.direccionPrincipal
            direccion.exterior = captura.exterior
            direccion.interior = captura.interior
            direccion.cp = captura.cp
            direccion.colonia = captura.colonia
            direccion.municipio = captura.municipio
            direccion.entidad = captura.entidad
            direccion.principal = true
            direccion.save(flush: true, failOnError: true)
            captura.cargado = true
            captura.save(flush: true, failOnError: true)
        }
    }

    def cargarContrato(CargadeContratos captura) {
        Marcas marca = Marcas.findByNombre(captura.marca)
//        Marcas marcaAuto = Marcas.findByNombre(auto.marca)
        if (marca == null) {
            Marcas altaMarca = new Marcas()
            altaMarca.nombre = captura.marca
            altaMarca.slug = captura.marca
            altaMarca.save(flush: true, failOnError: true)
            marca = Marcas.findByNombre(captura.marca)
        }

        Modelos modelo = Modelos.findByMarcaAndNombre(marca, captura.modelo)

//        Modelos modelo = Modelos.findByNombre(captura.modelo)
        if (modelo == null) {
            Modelos altaModelo = new Modelos()
            altaModelo.marca = marca
            altaModelo.nombre = captura.modelo
            altaModelo.slug = captura.modelo
            altaModelo.save(flush: true, failOnError: true)
            modelo = Modelos.findByMarcaAndNombre(marca, captura.modelo)
        }
//        Modelos gps = Modelos.findByNombre(captura.gps1)
        if (marca != null && modelo != null) {
            Contrato contrato = new Contrato()
//            contrato.numeroContrato = captura.numeroContrato
//            contrato.referencia = captura.referenciaBancaria
//            contrato.fechaContrato = captura.fechaOtorgamiento
//            contrato.rfc = captura.rfc
//            contrato.nombreLargo = captura.nombreDelCliente
//            contrato.marca = marca
//            contrato.modelo = modelo
//            contrato.anio = captura.anio
//            contrato.color = captura.color
//            contrato.placas = captura.placas
//            contrato.numeroVin = captura.numeroVin
//            contrato.montoRequerido = captura.montoPrestado
////        representanteLegal
////        contrato. rango
////        contrato. capturado
//            contrato.costoMensualInteres = captura.interes
//            contrato.costoMensualMonitoreo = captura.gps
//            contrato.costoMensualGPS = captura.monitoreo
//            contrato.contratoMonterrey = false
            contrato.regimenFiscal = C_RegimenFiscal.findByClave(captura.regimenFiscal)
            contrato.fechaContrato = captura.fechaContrato
//            contrato.nombres = captura.nombres
//            contrato.primerApellido = captura.primerApellido
//            contrato.segundoApellido = captura.segundoApellido
            contrato.genero = captura.genero
            contrato.rfc = captura.rfc
            if (captura.edad != "N/A") {
                contrato.edad = captura.edad as Long
            }
//            contrato.fechaNacimiento = captura.fechaNacimiento
            contrato.curp = captura.curp
//            contrato.claveElector = captura.claveElector
//            contrato.documentoOficial = captura.documentoOficial
//            contrato.telefonoFijo = captura.telefonoFijo
            contrato.telefonoCelular = captura.telefonoCelular
            contrato.telefonoOficina = captura.telefonoOficina
            contrato.correoElectronico = captura.correoElectronico
//            contrato.nombresCoacreditado = captura.nombresCoacreditado
//            contrato.primerApellidoCoacreditado = captura.primerApellidoCoacreditado
//            contrato.segundoApellidoCoacreditado = captura.segundoApellidoCoacreditado
//            contrato.generoCoacreditado = captura.generoCoacreditado
//            contrato.rfcCoacreditado = captura.rfcCoacreditado
//            contrato.edadCoacreditado = captura.edadCoacreditado
//            contrato.fechaNacimientoCoacreditado = captura.fechaNacimientoCoacreditado
//            contrato.curpCoacreditado = captura.curpCoacreditado
//            contrato.documentoOficialCoacreditado = captura.documentoOficialCoacreditado
//            contrato.claveElectorCoacreditado = captura.claveElectorCoacreditado
//            contrato.telefonoFijoCoacreditado = captura.telefonoFijoCoacreditado
//            contrato.telefonoCelularCoacreditado = captura.telefonoCelularCoacreditado
//            contrato.telefonoOficinaCoacreditado = captura.telefonoOficinaCoacreditado
//            contrato.correoElectronicoCoacreditado = captura.correoElectronicoCoacreditado
            contrato.anio = captura.anio
            contrato.marca = marca
            contrato.modelo = modelo
            contrato.versionAuto = captura.versionAuto
            contrato.color = captura.color
            contrato.placas = captura.placas
//            contrato.numeroDeMotor = captura.numeroDeMotor
//            contrato.numeroDeFactura = captura.numeroDeFactura
//            contrato.fechaDeFactura = captura.fechaDeFactura
//            contrato.emisoraDeFactura = captura.emisoraDeFactura
//            contrato.valorDeVenta = captura.valorDeVenta
            contrato.valorDeCompra = captura.valorDeCompra
            contrato.montoMaximoAutorizado = captura.montoMaximoAutorizado as BigDecimal
            contrato.numeroVin = captura.numeroVin
            contrato.gps1 = Gps.findByNombre(captura.gps1)
            if (captura.gps2 != 'Null') {
                contrato.gps2 = Gps.findByNombre(captura.gps2)
            }
//            contrato.gps3 = captura.gps3
//            contrato.montoRequerido = captura.montoRequerido
            contrato.costoMensualInteres = captura.costoMensualInteres
            contrato.costoMensualMonitoreo = captura.costoMensualMonitoreo
            contrato.costoMensualGPS = captura.costoMensualGPS
            contrato.totalAutoPresta = captura.totalAutoPresta
            contrato.iva = captura.iva
            contrato.costoMensualTotal = captura.costoMensualTotal
//            contrato.tipoContrato = captura.tipoContrato
            contrato.estatus = 'R'
            contrato.referencia = captura.referencia
            contrato.clabe = captura.clabe
//            contrato.razonesSociales = captura.razonesSociales
//            contrato.calificacionCliente = captura.calificacionCliente
            contrato.numeroContrato = captura.numeroContrato
            contrato.contratoPrueba = captura.contratoPrueba = false
            contrato.montoTransferencia = captura.montoTransferencia as BigDecimal
            contrato.detalleDescuentos = captura.detalleDescuentos
            contrato.fechaSolicitud = captura.fechaSolicitud
//            contrato.montoLiquidar = captura.montoLiquidar
            contrato.fechaCompromiso = captura.fechaCompromiso
//            contrato.estatusContrato = captura.estatusContrato
            contrato.contratoMonterrey = captura.contratoMonterrey = false
            contrato.nombreLargo = captura.nombreLargo
//            contrato.nombreLargoCoacreditado = captura.nombreLargoCoacreditado
            contrato.folioCarga = captura.id
            contrato.save(flush: true, failOnError: true)

            cargaDetalles(contrato)

            captura.cargado = true
            captura.save(flush: true, failOnError: true)
        }
//        contrato. total
//        contrato. iva
//        contrato. granTotal
//        contrato. fechaCorte
    }

    def cargaDetalles(Contrato contrato) {
        for (Integer i = 1; i <= 12; i++) {
            def fecha = contratoService.calcularFechaPago(i)
            ContratoDetalle contratoDetalle = new ContratoDetalle()
            contratoDetalle.contrato = contrato
            contratoDetalle.parcialidad = i
            contratoDetalle.fecha = fecha
            contratoDetalle.interes = contrato.costoMensualInteres
            contratoDetalle.monitoreo = contrato.costoMensualMonitoreo
            contratoDetalle.gps = contrato.costoMensualGPS
            contratoDetalle.capital = i == 12 ? montoRequeido(contrato) : 0
            contratoDetalle.subtotal = i == 12 ? (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS + montoRequeido(contrato)) : (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS)
            contratoDetalle.iva = (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS) * 0.16
            contratoDetalle.saldoFinal = i == 12 ? 0 : montoRequeido(contrato)
            contratoDetalle.save(flush: true, failOnError: true)
        }
    }

    BigDecimal montoRequeido(Contrato contrato){
        return contrato.montoTransferencia + new BigDecimal(contrato.detalleDescuentos)
    }
}
