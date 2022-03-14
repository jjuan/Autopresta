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
class CargadeContratosController extends CatalogoController<CargadeContratos> {
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
            contrato.numeroContrato = captura.numeroContrato
            contrato.referencia = captura.referencia
            contrato.fechaContrato = captura.fechaFin
            contrato.rfc = captura.rfc
            contrato.nombreLargo = captura.nombreLargo
            contrato.marca = marca
            contrato.modelo = modelo
            contrato.anio = captura.anio
            contrato.color = captura.color
            contrato.placas = captura.placas
            contrato.numeroVin = captura.numeroVin
            contrato.montoRequerido = captura.montoActualReq
            //        representanteLegal
//        contrato. rango
//        contrato. capturado
            contrato.costoMensualInteres = captura.costoMensualInteres
            contrato.costoMensualMonitoreo = captura.costoMensualMonitoreo
            contrato.costoMensualGPS = captura.costoMensualGPS
            contrato.contratoMonterrey = false
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
            contrato.anio = captura.anio
            contrato.marca = marca
            contrato.modelo = modelo
            contrato.versionAuto = captura.versionAuto
            contrato.color = captura.color
            contrato.placas = captura.placas
            contrato.valorDeCompra = captura.valorDeCompra
            contrato.montoMaximoAutorizado = captura.montoMaximoAutorizado as BigDecimal
            contrato.numeroVin = captura.numeroVin
            contrato.gps1 = Gps.findByNombre(captura.gps1)
            if (captura.gps2 != 'Null') {
                contrato.gps2 = Gps.findByNombre(captura.gps2)
            }
            contrato.costoMensualInteres = captura.costoMensualInteres
            contrato.costoMensualMonitoreo = captura.costoMensualMonitoreo
            contrato.costoMensualGPS = captura.costoMensualGPS
            contrato.totalAutoPresta = captura.totalAutoPresta
            contrato.iva = captura.iva
            contrato.costoMensualTotal = captura.costoMensualTotal
            contrato.estatus = 'R'
            contrato.referencia = captura.referencia
            contrato.clabe = captura.clabe
            contrato.numeroContrato = captura.numeroContrato
            contrato.contratoPrueba = captura.contratoPrueba = false
            contrato.montoTransferencia = captura.montoTransferencia as BigDecimal
            contrato.detalleDescuentos = captura.detalleDescuentos
            contrato.fechaSolicitud = captura.fechaSolicitud
            contrato.fechaCompromiso = captura.fechaCompromiso
            contrato.contratoMonterrey = captura.contratoMonterrey = false
            contrato.nombreLargo = captura.nombreLargo
            contrato.folioCarga = captura.id
            contrato.save(flush: true, failOnError: true)

            cargaDetalles(contrato, captura.inicio, captura.fin)

            captura.cargado = true
            captura.save(flush: true, failOnError: true)
        }
    }

    def cargaDetalles(Contrato contrato, Integer inicio, Integer fin) {
        for (Integer i = inicio; i <= fin; i++) {
            def costoMensualInteres = (contrato.montoRequerido * 5)/100
            def costoMensualMonitoreo = (contrato.montoRequerido * 1)/100<800?800:(contrato.montoRequerido * 1)/100
            def costoMensualGPS = (contrato.montoRequerido * 0.75)/100<600?600:(contrato.montoRequerido * 0.75)/100

            def fecha = contratoService.calcularFechaPago(i, contrato.fechaContrato)
            ContratoDetalle contratoDetalle = new ContratoDetalle()
            contratoDetalle.contrato = contrato
            contratoDetalle.parcialidad = i
            contratoDetalle.fecha = fecha
            contratoDetalle.interes = costoMensualInteres
            contratoDetalle.monitoreo = costoMensualMonitoreo
            contratoDetalle.gps = costoMensualGPS
            contratoDetalle.capital = i == 12 ? montoRequeido(contrato) : 0
            contratoDetalle.subtotal = i == 12 ? (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS + montoRequeido(contrato)) : (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS)
            contratoDetalle.iva = (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS) * 0.16
            contratoDetalle.saldoFinal = i == 12 ? 0 : montoRequeido(contrato)
            contratoDetalle.save(flush: true, failOnError: true)
        }
    }

    BigDecimal montoRequeido(Contrato contrato) {
        return contrato.montoTransferencia + new BigDecimal(contrato.detalleDescuentos)
    }

    def extension1() {
        def extensiones = CargadeContratos.findAllByNumeroExtensiones(2.00)
        for (extension in extensiones) {
            generaContratoExtendido(extension)
        }
        respond message: 'ok'
    }

    def generaContratoExtendido(CargadeContratos cargadeContrato) {
        Contrato historico = Contrato.findByFolioCarga(cargadeContrato.id)
        Contrato extension = new Contrato()
        extension.regimenFiscal = historico.regimenFiscal
        extension.fechaContrato = ContratoDetalle.findByParcialidadAndContrato('12', historico).fecha
        extension.nombres = historico.nombres
        extension.primerApellido = historico.primerApellido
        extension.segundoApellido = historico.segundoApellido
        extension.genero = historico.genero
        extension.rfc = historico.rfc
        extension.edad = historico.edad
        extension.fechaNacimiento = historico.fechaNacimiento
        extension.curp = historico.curp
        extension.claveElector = historico.claveElector
        extension.documentoOficial = historico.documentoOficial
        extension.telefonoFijo = historico.telefonoFijo
        extension.telefonoCelular = historico.telefonoCelular
        extension.telefonoOficina = historico.telefonoOficina
        extension.correoElectronico = historico.correoElectronico
        extension.nombresCoacreditado = historico.nombresCoacreditado
        extension.primerApellidoCoacreditado = historico.primerApellidoCoacreditado
        extension.segundoApellidoCoacreditado = historico.segundoApellidoCoacreditado
        extension.generoCoacreditado = historico.generoCoacreditado
        extension.rfcCoacreditado = historico.rfcCoacreditado
        extension.edadCoacreditado = historico.edadCoacreditado
        extension.fechaNacimientoCoacreditado = historico.fechaNacimientoCoacreditado
        extension.curpCoacreditado = historico.curpCoacreditado
        extension.documentoOficialCoacreditado = historico.documentoOficialCoacreditado
        extension.claveElectorCoacreditado = historico.claveElectorCoacreditado
        extension.telefonoFijoCoacreditado = historico.telefonoFijoCoacreditado
        extension.telefonoCelularCoacreditado = historico.telefonoCelularCoacreditado
        extension.telefonoOficinaCoacreditado = historico.telefonoOficinaCoacreditado
        extension.correoElectronicoCoacreditado = historico.correoElectronicoCoacreditado
        extension.anio = historico.anio
        extension.marca = historico.marca
        extension.modelo = historico.modelo
        extension.versionAuto = historico.versionAuto
        extension.color = historico.color
        extension.placas = historico.placas
        extension.numeroDeMotor = historico.numeroDeMotor
        extension.numeroDeFactura = historico.numeroDeFactura
        extension.fechaDeFactura = historico.fechaDeFactura
        extension.emisoraDeFactura = historico.emisoraDeFactura
        extension.valorDeVenta = historico.valorDeVenta
        extension.valorDeCompra = historico.valorDeCompra
        extension.montoMaximoAutorizado = historico.montoMaximoAutorizado
        extension.numeroVin = historico.numeroVin
        extension.gps1 = historico.gps1
        extension.gps2 = historico.gps2
        extension.gps3 = historico.gps3
        extension.montoRequerido = montoRequeido(historico) - (montoRequeido(historico) * 0.2 * 1)
        extension.costoMensualInteres = historico.costoMensualInteres
        extension.costoMensualMonitoreo = historico.costoMensualMonitoreo
        extension.costoMensualGPS = historico.costoMensualGPS
        extension.totalAutoPresta = historico.totalAutoPresta
        extension.iva = historico.iva
        extension.costoMensualTotal = historico.costoMensualTotal
        extension.tipoContrato = historico.tipoContrato
        extension.estatus = historico.estatus
        extension.referencia = historico.referencia
        extension.clabe = historico.clabe
        extension.razonesSociales = historico.razonesSociales
        extension.calificacionCliente = historico.calificacionCliente
        extension.numeroContrato = historico.numeroContrato
        extension.contratoPrueba = historico.contratoPrueba
        extension.montoTransferencia = historico.montoTransferencia
        extension.detalleDescuentos = historico.detalleDescuentos
        extension.fechaSolicitud = historico.fechaSolicitud
        extension.montoLiquidar = historico.montoLiquidar
        extension.fechaCompromiso = historico.fechaCompromiso
        extension.estatusContrato = historico.estatusContrato
        extension.contratoMonterrey = historico.contratoMonterrey
        extension.nombreLargo = historico.nombreLargo
        extension.nombreLargoCoacreditado = historico.nombreLargoCoacreditado
        extension.folioCarga = historico.folioCarga
        extension.save(flush: true, failOnError: true)
        cargaDetalles(extension)
    }

    def obtenerFecha() {
        def carga = CargadeContratos.findAllByInicioGreaterThan(12)
        for (contrato in carga) {
def fecha = contratoService.calcularFechaPago(contrato.inicio - 1, contrato.fechaContrato)
            CargadeContratos cargadeContratos = CargadeContratos.findById(contrato.id)
            cargadeContratos.fechaFin=fecha
            cargadeContratos.save(flush: true, failOnError: true)
        }

        respond message: 'ok'
    }
}
