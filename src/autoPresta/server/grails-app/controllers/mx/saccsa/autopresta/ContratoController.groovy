package mx.saccsa.autopresta

import grails.rest.RestfulController
import grails.validation.ValidationException
import mx.saccsa.common.Parametros

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class ContratoController extends RestfulController<Contrato> {
    ContratoController() {
        super(Contrato)
    }
    def contratoService
    def folioService

    def index() {
        String validar = Parametros.getValorByParametro('Pruebas')
        def contratos
        if (validar == '1') {
            contratos = Contrato.findAllByEstatusNotEqual('F')
        } else {
            contratos = Contrato.findAllByContratoPruebaAndEstatusNotEqual(false, 'F')
        }
        def lista = contratos.collect({
            [
                    id                           : it?.id,
                    regimenFiscal                : it?.regimenFiscal?.descLabel,
                    titular                      : it.razonesSociales ? it.razonesSociales.descLabel : it.nombres + ' ' + it.primerApellido + ' ' + it.segundoApellido,
                    representante                : it.razonesSociales ? it.nombres + ' ' + it.primerApellido + ' ' + it.segundoApellido : '',
                    fechaContrato                : it?.fechaContrato,
                    nombres                      : it?.nombres,
                    primerApellido               : it?.primerApellido,
                    segundoApellido              : it?.segundoApellido,
                    genero                       : it?.genero,
                    rfc                          : it?.rfc,
                    edad                         : it?.edad,
                    fechaNacimiento              : it?.fechaNacimiento,
                    curp                         : it?.curp,
                    claveElector                 : it?.claveElector,
                    documentoOficial             : it?.documentoOficial,
                    telefonoFijo                 : it?.telefonoFijo,
                    telefonoCelular              : it?.telefonoCelular,
                    telefonoOficina              : it?.telefonoOficina,
                    correoElectronico            : it?.correoElectronico,
                    nombresCoacreditado          : it?.nombresCoacreditado,
                    primerApellidoCoacreditado   : it?.primerApellidoCoacreditado,
                    segundoApellidoCoacreditado  : it?.segundoApellidoCoacreditado,
                    generoCoacreditado           : it?.generoCoacreditado,
                    rfcCoacreditado              : it?.rfcCoacreditado,
                    edadCoacreditado             : it?.edadCoacreditado,
                    fechaNacimientoCoacreditado  : it?.fechaNacimientoCoacreditado,
                    curpCoacreditado             : it?.curpCoacreditado,
                    documentoOficialCoacreditado : it?.documentoOficialCoacreditado?.nombre,
                    claveElectorCoacreditado     : it?.claveElectorCoacreditado,
                    telefonoFijoCoacreditado     : it?.telefonoFijoCoacreditado,
                    telefonoCelularCoacreditado  : it?.telefonoCelularCoacreditado,
                    telefonoOficinaCoacreditado  : it?.telefonoOficinaCoacreditado,
                    correoElectronicoCoacreditado: it?.correoElectronicoCoacreditado,
                    anio                         : it?.anio,
                    marca                        : it?.marca?.nombre,
                    modelo                       : it?.modelo?.nombre,
                    versionAuto                  : it?.versionAuto,
                    color                        : it?.color,
                    placas                       : it?.placas,
                    numeroDeMotor                : it?.numeroDeMotor,
                    numeroDeFactura              : it?.numeroDeFactura,
                    fechaDeFactura               : it?.fechaDeFactura,
                    emisoraDeFactura             : it?.emisoraDeFactura,
                    valorDeVenta                 : it?.valorDeVenta,
                    valorDeCompra                : it?.valorDeCompra,
                    montoMaximoAutorizado        : it?.montoMaximoAutorizado,
                    numeroVin                    : it?.numeroVin,
                    gps1                         : it?.gps1?.descLabel,
                    gps2                         : it?.gps2?.descLabel,
                    gps3                         : it?.gps3?.descLabel,
                    montoRequerido               : it?.montoRequerido,
                    costoMensualInteres          : it?.costoMensualInteres,
                    costoMensualMonitoreo        : it?.costoMensualMonitoreo,
                    costoMensualGPS              : it?.costoMensualGPS,
                    totalAutoPresta              : it?.totalAutoPresta,
                    iva                          : it?.iva,
                    costoMensualTotal            : it?.costoMensualTotal,
                    tipoContrato                 : it?.tipoContrato?.descLabel,
                    estatus                      : it?.estatus,
                    referencia                   : it?.referencia,
                    clabe                        : it?.clabe,
                    razonesSociales              : it?.razonesSociales ? it.razonesSociales.descLabel : '',
                    calificacionCliente          : it?.calificacionCliente?.descLabel,
                    numeroContrato               : it.numeroContrato != '' ? contratoFolio(it.numeroContrato, it.contratoPrueba) : '',
                    contratoPrueba               : it?.contratoPrueba,
                    montoTransferencia           : it?.montoTransferencia,
                    detalleDescuentos            : it?.detalleDescuentos,
                    fechaSolicitud               : it?.fechaSolicitud,
                    montoLiquidar                : it?.montoLiquidar,
                    fechaCompromiso              : it?.fechaCompromiso,
                    total                        : ContratoDetalle.findAllByContrato(Contrato.findById(it.id)).collect({ [monto: it.subtotal + it.iva] }),
                    direccion                    : Direccion.findAllByContrato(Contrato.findById(it.id)).collect({
                        [
                                id                : it.id,
                                contrato          : it.contrato.numeroContrato,
                                dirTrabajo        : it.dirTrabajo,
                                dirAdicional      : it.dirAdicional,
                                direccionPrincipal: it.direccionPrincipal,
                                exterior          : it.exterior,
                                interior          : it.interior,
                                cp                : it.cp,
                                colonia           : it.colonia,
                                municipio         : it.municipio,
                                entidad           : it.entidad,
                                principal         : it.principal,
                        ]
                    })
            ]
        })

        lista = lista.sort({ it.fechaContrato }).reverse()
        respond(lista)
    }

    def contratosFirmados() {
        String validar = Parametros.getValorByParametro('Pruebas')
        def contratos
        if (validar == '1') {
            contratos = Contrato.findAllByEstatus('F')
        } else {
            contratos = Contrato.findAllByContratoPruebaAndEstatus(false, 'F')
        }
        def lista = contratos.collect({
            [
                    id                           : it?.id,
                    regimenFiscal                : it?.regimenFiscal?.descLabel,
                    titular                      : it.razonesSociales ? it.razonesSociales.descLabel : it.nombres + ' ' + it.primerApellido + ' ' + it.segundoApellido,
                    representante                : it.razonesSociales ? it.nombres + ' ' + it.primerApellido + ' ' + it.segundoApellido : '',
                    fechaContrato                : it?.fechaContrato,
                    nombres                      : it?.nombres,
                    primerApellido               : it?.primerApellido,
                    segundoApellido              : it?.segundoApellido,
                    genero                       : it?.genero,
                    rfc                          : it?.rfc,
                    edad                         : it?.edad,
                    fechaNacimiento              : it?.fechaNacimiento,
                    curp                         : it?.curp,
                    claveElector                 : it?.claveElector,
                    documentoOficial             : it?.documentoOficial,
                    telefonoFijo                 : it?.telefonoFijo,
                    telefonoCelular              : it?.telefonoCelular,
                    telefonoOficina              : it?.telefonoOficina,
                    correoElectronico            : it?.correoElectronico,
                    nombresCoacreditado          : it?.nombresCoacreditado,
                    primerApellidoCoacreditado   : it?.primerApellidoCoacreditado,
                    segundoApellidoCoacreditado  : it?.segundoApellidoCoacreditado,
                    generoCoacreditado           : it?.generoCoacreditado,
                    rfcCoacreditado              : it?.rfcCoacreditado,
                    edadCoacreditado             : it?.edadCoacreditado,
                    fechaNacimientoCoacreditado  : it?.fechaNacimientoCoacreditado,
                    curpCoacreditado             : it?.curpCoacreditado,
                    documentoOficialCoacreditado : it?.documentoOficialCoacreditado?.nombre,
                    claveElectorCoacreditado     : it?.claveElectorCoacreditado,
                    telefonoFijoCoacreditado     : it?.telefonoFijoCoacreditado,
                    telefonoCelularCoacreditado  : it?.telefonoCelularCoacreditado,
                    telefonoOficinaCoacreditado  : it?.telefonoOficinaCoacreditado,
                    correoElectronicoCoacreditado: it?.correoElectronicoCoacreditado,
                    anio                         : it?.anio,
                    marca                        : it?.marca?.nombre,
                    modelo                       : it?.modelo?.nombre,
                    versionAuto                  : it?.versionAuto,
                    color                        : it?.color,
                    placas                       : it?.placas,
                    numeroDeMotor                : it?.numeroDeMotor,
                    numeroDeFactura              : it?.numeroDeFactura,
                    fechaDeFactura               : it?.fechaDeFactura,
                    emisoraDeFactura             : it?.emisoraDeFactura,
                    valorDeVenta                 : it?.valorDeVenta,
                    valorDeCompra                : it?.valorDeCompra,
                    montoMaximoAutorizado        : it?.montoMaximoAutorizado,
                    numeroVin                    : it?.numeroVin,
                    gps1                         : it?.gps1?.descLabel,
                    gps2                         : it?.gps2?.descLabel,
                    gps3                         : it?.gps3?.descLabel,
                    montoRequerido               : it?.montoRequerido,
                    costoMensualInteres          : it?.costoMensualInteres,
                    costoMensualMonitoreo        : it?.costoMensualMonitoreo,
                    costoMensualGPS              : it?.costoMensualGPS,
                    totalAutoPresta              : it?.totalAutoPresta,
                    iva                          : it?.iva,
                    costoMensualTotal            : it?.costoMensualTotal,
                    tipoContrato                 : it?.tipoContrato?.descLabel,
                    estatus                      : it?.estatus,
                    referencia                   : it?.referencia,
                    clabe                        : it?.clabe,
                    razonesSociales              : it?.razonesSociales ? it.razonesSociales.descLabel : '',
                    calificacionCliente          : it?.calificacionCliente?.descLabel,
                    numeroContrato               : it.numeroContrato != '' ? contratoFolio(it.numeroContrato, it.contratoPrueba) : '',
                    contratoPrueba               : it?.contratoPrueba,
                    montoTransferencia           : it?.montoTransferencia,
                    detalleDescuentos            : it?.detalleDescuentos,
                    fechaSolicitud               : it?.fechaSolicitud,
                    montoLiquidar                : it?.montoLiquidar,
                    fechaCompromiso              : it?.fechaCompromiso,
                    estatusContrato              : it?.estatusContrato,
                    total                        : ContratoDetalle.findAllByContrato(Contrato.findById(it.id)).collect({ [monto: it.subtotal + it.iva] }),
                    direccion                    : Direccion.findAllByContrato(Contrato.findById(it.id)).collect({
                        [
                                id                : it.id,
                                contrato          : it.contrato.numeroContrato,
                                dirTrabajo        : it.dirTrabajo,
                                dirAdicional      : it.dirAdicional,
                                direccionPrincipal: it.direccionPrincipal,
                                exterior          : it.exterior,
                                interior          : it.interior,
                                cp                : it.cp,
                                colonia           : it.colonia,
                                municipio         : it.municipio,
                                entidad           : it.entidad,
                                principal         : it.principal,
                        ]
                    })
            ]
        })

        lista = lista.sort({ it.fechaContrato }).reverse()
        respond(lista)
    }

    @Transactional
    def save() {
        params
        request.JSON
        Boolean principal = true
        Contrato contrato = resource.newInstance()
        bindData contrato, request.JSON

        if (contrato.contratoPrueba) {
            def folio = getFolioRecuperado('ContratoPruebas')
            if (folio == 0) {
                contrato.numeroContrato = folioService.generaFolio('ContratoPruebas').toString() + 'P'
            } else {
                contrato.numeroContrato = folio.toString() + 'P'
                FoliosRecuperados foliosRecuperado = FoliosRecuperados.findByCveTipoAndFolio('ContratoPruebas', folio.toString())
                foliosRecuperado.delete(flush: true, failOnError: true)
            }
        } else {
            def folio = getFolioRecuperado('Contrato')
            if (folio == 0) {
                contrato.numeroContrato = folioService.generaFolio('Contrato').toString()
            } else {
                contrato.numeroContrato = folio.toString()
                FoliosRecuperados foliosRecuperado = FoliosRecuperados.findByCveTipoAndFolio('Contrato', folio.toString())
                foliosRecuperado.delete(flush: true, failOnError: true)
            }
        }

        contrato.validate()
        contrato.save(flush: true, failOnError: true)

        if (request.JSON.regimenFiscal == 'PM') {
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

        for (Integer i = 1; i <= 12; i++) {
            def fecha = contratoService.calcularFechaPago(i)
            ContratoDetalle contratoDetalle = new ContratoDetalle()
            contratoDetalle.contrato = contrato
            contratoDetalle.parcialidad = i
            contratoDetalle.fecha = fecha
            contratoDetalle.interes = contrato.costoMensualInteres
            contratoDetalle.monitoreo = contrato.costoMensualMonitoreo
            contratoDetalle.gps = contrato.costoMensualGPS
            contratoDetalle.capital = i == 12 ? contrato.montoRequerido : 0
            contratoDetalle.subtotal = i == 12 ? (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS + contrato.montoRequerido) : (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS)
            contratoDetalle.iva = (contrato.costoMensualInteres + contrato.costoMensualMonitoreo + contrato.costoMensualGPS) * 0.16
            contratoDetalle.saldoFinal = i == 12 ? 0 : contrato.montoRequerido
            contratoDetalle.save(flush: true, failOnError: true)
        }
        respond(contrato)
    }

    def folios() {
        Folios contrato = Folios.findByCveTipo('Contrato')
        Folios contratoPrueba = Folios.findByCveTipo('ContratoPruebas')

        def folioRecuperadoP = getFolioRecuperado('ContratoPruebas')
        def folioRecuperado = getFolioRecuperado('Contrato')


        String folio = contrato != null ? (contrato.folio + 1).toString() : '1'
        String folioPrueba = contratoPrueba != null ? (contratoPrueba.folio + 1).toString() + 'P' : '1P'
        respond(folio: folioRecuperado > 0 ? folioRecuperado : folio, folioPrueba: folioRecuperadoP > 0 ? folioRecuperadoP + 'P' : folioPrueba)
    }

    def cambioStatus(Long id) {
        Contrato contrato = Contrato.findById(id)
        contrato.estatus = 'I'
        contrato.save(flush: true, failOnError: true)
    }

    def librarFolio(Long id) {
        String folio
        Contrato contrato = Contrato.findById(id)
        folio = contrato.numeroContrato
        FoliosRecuperados foliosRecuperados = new FoliosRecuperados()
        if (contrato.contratoPrueba) {
            foliosRecuperados.cveTipo = 'ContratoPruebas'
            def folios = folio.split('P')
            folio = folios[0]
            log.error '' + folio
        } else {
            foliosRecuperados.cveTipo = 'Contrato'
        }
        foliosRecuperados.folio = folio
        foliosRecuperados.save(flush: true, failOnError: true)

        contrato.numeroContrato = ''
        contrato.estatus = 'C'
        contrato.save(flush: true, failOnError: true)
        respond(message: 'Folio recuperado')
    }

    def firmar(Long id) {
        Contrato contrato = Contrato.findById(id)
        contrato.estatus = 'F'
        contrato.estatusContrato = 'Activo'
        contrato.save(flush: true, failOnError: true)
        respond(message: 'Folio: ' + contrato.numeroContrato + ' firmado')
    }


    def getFolioRecuperado(String cveTipo) {
        def validar = FoliosRecuperados.findAllByCveTipo(cveTipo)
        if (validar.size() > 0) {
            def folioRecuperado = FoliosRecuperados.executeQuery('Select min(folio) from FoliosRecuperados where cveTipo= :cveTipo', [cveTipo: cveTipo])
            return new Long(folioRecuperado[0] as String)
        } else {
            return 0
        }
    }

    String contratoFolio(String folio, Boolean contratoPrueba) {
        if (contratoPrueba) {
            if (folio.length() <= 5) {
                folio = '0' + folio
            }
        } else {
            if (folio.length() <= 4) {
                folio = '0' + folio
            }
        }
        return folio
    }
}
