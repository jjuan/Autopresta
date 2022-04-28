package mx.saccsa.autopresta

import grails.rest.RestfulController
import grails.validation.ValidationException
import mx.saccsa.common.Parametros

import java.text.SimpleDateFormat

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
    SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')
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
                    numeroContrato               : it.numeroContrato != '' ? contratoFolio(it.numeroContrato, it.tipoFolio) : '',
                    contratoPrueba               : it?.contratoPrueba,
                    montoTransferencia           : it?.montoTransferencia,
                    detalleDescuentos            : it?.detalleDescuentos,
                    fechaSolicitud               : it?.fechaSolicitud,
                    montoLiquidar                : it?.montoLiquidar,
                    fechaCompromiso              : it?.fechaCompromiso,
                    estatusLabel                 : getEstatus(it.estatus),
                    total                        : ContratoDetalle.findAllByContrato(Contrato.findById(it.id)).collect({ [monto: it.subtotal + it.iva] }),
                    direccion                    : getDirecciones(it)
            ]
        })

        lista = lista.sort({ it.fechaContrato }).reverse()
        respond(lista)
    }

    def getDirecciones(Contrato contrato) {
        return Direccion.findAllByContrato(contrato).collect({
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
    }

    def getContrato(Long id) {
        def contrato = Contrato.findById(id).collect({
            [
                    regimenFiscal                : it.regimenFiscal != null ? it.regimenFiscal.clave : '',
                    fechaContrato                : it.fechaContrato != null ? it.fechaContrato : '',
                    nombres                      : it.nombres != null ? it.nombres : '',
                    primerApellido               : it.primerApellido != null ? it.primerApellido : '',
                    segundoApellido              : it.segundoApellido != null ? it.segundoApellido : '',
                    genero                       : it.genero != null ? it.genero : '',
                    rfc                          : it.rfc != null ? it.rfc : '',
                    edad                         : it.edad != null ? it.edad : '',
                    fechaNacimiento              : it.fechaNacimiento != null ? it.fechaNacimiento : '',
                    curp                         : it.curp != null ? it.curp : '',
                    claveElector                 : it.claveElector != null ? it.claveElector : '',
                    documentoOficial             : it.documentoOficial != null ? it.documentoOficial : '',
                    telefonoFijo                 : it.telefonoFijo != null ? it.telefonoFijo : '',
                    telefonoCelular              : it.telefonoCelular != null ? it.telefonoCelular : '',
                    telefonoOficina              : it.telefonoOficina != null ? it.telefonoOficina : '',
                    correoElectronico            : it.correoElectronico != null ? it.correoElectronico : '',
                    nombresCoacreditado          : it.nombresCoacreditado != null ? it.nombresCoacreditado : '',
                    primerApellidoCoacreditado   : it.primerApellidoCoacreditado != null ? it.primerApellidoCoacreditado : '',
                    segundoApellidoCoacreditado  : it.segundoApellidoCoacreditado != null ? it.segundoApellidoCoacreditado : '',
                    generoCoacreditado           : it.generoCoacreditado != null ? it.generoCoacreditado : '',
                    rfcCoacreditado              : it.rfcCoacreditado != null ? it.rfcCoacreditado : '',
                    edadCoacreditado             : it.edadCoacreditado != null ? it.edadCoacreditado : '',
                    fechaNacimientoCoacreditado  : it.fechaNacimientoCoacreditado != null ? it.fechaNacimientoCoacreditado : '',
                    curpCoacreditado             : it.curpCoacreditado != null ? it.curpCoacreditado : '',
                    documentoOficialCoacreditado : it.documentoOficialCoacreditado != null ? it.documentoOficialCoacreditado : '',
                    claveElectorCoacreditado     : it.claveElectorCoacreditado != null ? it.claveElectorCoacreditado : '',
                    telefonoFijoCoacreditado     : it.telefonoFijoCoacreditado != null ? it.telefonoFijoCoacreditado : '',
                    telefonoCelularCoacreditado  : it.telefonoCelularCoacreditado != null ? it.telefonoCelularCoacreditado : '',
                    telefonoOficinaCoacreditado  : it.telefonoOficinaCoacreditado != null ? it.telefonoOficinaCoacreditado : '',
                    correoElectronicoCoacreditado: it.correoElectronicoCoacreditado != null ? it.correoElectronicoCoacreditado : '',
                    anio                         : it.anio != null ? it.anio : '',
                    marca                        : it.marca != null ? it.marca.id : '',
                    modelo                       : it.modelo != null ? it.modelo.id : '',
                    versionAuto                  : it.versionAuto != null ? it.versionAuto : '',
                    color                        : it.color != null ? it.color : '',
                    placas                       : it.placas != null ? it.placas : '',
                    numeroDeMotor                : it.numeroDeMotor != null ? it.numeroDeMotor : '',
                    numeroDeFactura              : it.numeroDeFactura != null ? it.numeroDeFactura : '',
                    fechaDeFactura               : it.fechaDeFactura != null ? it.fechaDeFactura : '',
                    emisoraDeFactura             : it.emisoraDeFactura != null ? it.emisoraDeFactura : '',
                    valorDeVenta                 : it.valorDeVenta != null ? it.valorDeVenta : '',
                    valorDeCompra                : it.valorDeCompra != null ? it.valorDeCompra : '',
                    montoMaximoAutorizado        : it.montoMaximoAutorizado != null ? it.montoMaximoAutorizado : '',
                    numeroVin                    : it.numeroVin != null ? it.numeroVin : '',
                    gps1                         : it.gps1 != null ? it.gps1.id : '',
                    gps2                         : it.gps2 != null ? it.gps2.id : '',
                    gps3                         : it.gps3 != null ? it.gps3.id : '',
                    montoRequerido               : it.montoRequerido != null ? it.montoRequerido : '',
                    costoMensualInteres          : it.costoMensualInteres != null ? it.costoMensualInteres : '',
                    costoMensualMonitoreo        : it.costoMensualMonitoreo != null ? it.costoMensualMonitoreo : '',
                    costoMensualGPS              : it.costoMensualGPS != null ? it.costoMensualGPS : '',
                    totalAutoPresta              : it.totalAutoPresta != null ? it.totalAutoPresta : '',
                    iva                          : it.iva != null ? it.iva : '',
                    costoMensualTotal            : it.costoMensualTotal != null ? it.costoMensualTotal : '',
                    tipoContrato                 : it.tipoContrato != null ? it.tipoContrato.id : '',
                    estatus                      : it.estatus != null ? it.estatus : '',
                    referencia                   : it.referencia != null ? it.referencia : '',
                    clabe                        : it.clabe != null ? it.clabe : '',
                    razonesSociales              : it.razonesSociales != null ? [
                            razonSocial         : it.razonesSociales.razonSocial ? it.razonesSociales.razonSocial : '',
                            rfc                 : it.razonesSociales.rfc ? it.razonesSociales.rfc : '',
                            telefonoFijo        : it.razonesSociales.telefonoFijo ? it.razonesSociales.telefonoFijo : '',
                            telefonoCelular     : it.razonesSociales.telefonoCelular ? it.razonesSociales.telefonoCelular : '',
                            telefonoOficina     : it.razonesSociales.telefonoOficina ? it.razonesSociales.telefonoOficina : '',
                            calleDireccionFiscal: it.razonesSociales.calleDireccionFiscal ? it.razonesSociales.calleDireccionFiscal : '',
                            numeroExterior      : it.razonesSociales.numeroExterior ? it.razonesSociales.numeroExterior : '',
                            numeroInterior      : it.razonesSociales.numeroInterior ? it.razonesSociales.numeroInterior : '',
                            codigoPostal        : it.razonesSociales.codigoPostal ? it.razonesSociales.codigoPostal : '',
                            colonia             : it.razonesSociales.colonia ? it.razonesSociales.colonia : '',
                            entidad             : it.razonesSociales.entidad ? it.razonesSociales.entidad : '',
                            municipio           : it.razonesSociales.municipio ? it.razonesSociales.municipio : '',
                    ] : '',
                    calificacionCliente          : it.calificacionCliente != null ? it.calificacionCliente : '',
                    numeroContrato               : it.numeroContrato != null ? it.numeroContrato : '',
                    contratoPrueba               : it.contratoPrueba != null ? it.contratoPrueba : '',
                    montoTransferencia           : it.montoTransferencia != null ? it.montoTransferencia : '',
                    detalleDescuentos            : it.detalleDescuentos != null ? it.detalleDescuentos : '',
                    fechaSolicitud               : it.fechaSolicitud != null ? it.fechaSolicitud : '',
                    montoLiquidar                : it.montoLiquidar != null ? it.montoLiquidar : '',
                    fechaCompromiso              : it.fechaCompromiso != null ? it.fechaCompromiso : '',
                    estatusContrato              : it.estatusContrato != null ? it.estatusContrato : '',
                    contratoMonterrey            : it.contratoMonterrey != null ? it.contratoMonterrey : '',
                    nombreLargo                  : it.nombreLargo != null ? it.nombreLargo : '',
                    nombreLargoCoacreditado      : it.nombreLargoCoacreditado != null ? it.nombreLargoCoacreditado : '',
                    folioCarga                   : it.folioCarga != null ? it.folioCarga : '',
                    direcciones                  : getDirecciones(it)
            ]
        })
        respond contrato
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
                    numeroContrato               : it.numeroContrato != '' ? contratoFolio(it.numeroContrato, it.tipoFolio) : '',
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
        def folio = getFolioRecuperado(getClave(contrato.tipoFolio))
        if (folio == 0) {
            contrato.numeroContrato = contrato.tipoFolio=='P'?folioService.generaFolio(getClave(contrato.tipoFolio)).toString()+'P':folioService.generaFolio(getClave(contrato.tipoFolio)).toString()
        } else {
            contrato.numeroContrato = contrato.tipoFolio=='P'?folio.toString()+'P':folio.toString()
            FoliosRecuperados foliosRecuperado = FoliosRecuperados.findByCveTipoAndFolio(getClave(contrato.tipoFolio), folio.toString())
            foliosRecuperado.delete(flush: true, failOnError: true)
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
            contratoDetalle.estatus = 'P'
            contratoDetalle.save(flush: true, failOnError: true)
        }
        respond(contrato)
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

        foliosRecuperados.cveTipo = getClave(contrato.tipoFolio)
        foliosRecuperados.folio = contrato.tipoFolio=='P'? { def folios = folio.split('P'); folios[0]}:folio

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

    @Transactional
    def verificarFR() {
        def folios = FoliosRecuperados.findAllByFolioOrFolioOrFolioIsEmptyOrFolioNotIsNull(' ','')
        if (folios.size()>0) {
            for (folio in folios) {
                folio.delete(flush: true, failOnError: true)
            }
        }
    }

    def getFolioRecuperado(String cveTipo) {
//        verificarFR();
        def validar = FoliosRecuperados.findAllByCveTipo(cveTipo)
        if (validar.size() > 0) {
            def folioRecuperado = FoliosRecuperados.executeQuery('Select min(folio) from FoliosRecuperados where cveTipo= :cveTipo', [cveTipo: cveTipo])
            return new Long(folioRecuperado[0] as String)
        } else {
            return 0
        }
    }

    String contratoFolio(String folio, String tipoFolio) {
        switch (tipoFolio) {
            case 'CDMX':
                while (folio.length() < 5) {
                    folio = '0' + folio
                }
                break
            case 'GDL':
                while (folio.length() < 5) {
                    folio = '0' + folio
                }
                folio = 'GDL' + folio
                break
            case 'MTY':
                while (folio.length() < 5) {
                    folio = '0' + folio
                }
                folio = 'MTY' + folio
                break
            case 'P':
                folio = folio + 'P'
                while (folio.length() < 6) {
                    folio = '0' + folio
                }
                break
        }
        return folio
    }

    def estatusContratos() {
        def registrado = Contrato.countByEstatusAndContratoPrueba('R', false)
        def impreso = Contrato.countByEstatusAndContratoPrueba('I', false)
        def firmado = Contrato.countByEstatusAndContratoPrueba('F', false)
        def cancelado = Contrato.countByEstatusAndContratoPrueba('C', false)
        def total = registrado + impreso + firmado + cancelado

        respond([
                registrado: registrado,
                impreso   : impreso,
                firmado   : firmado,
                cancelado : cancelado,
                total     : total
        ])
    }

    def getEstatus(String estatus) {
        String label = ''
        if (estatus == 'C') {
            label = 'Cancelado'
        } else if (estatus == 'F') {
            label = 'Firmado'
        } else if (estatus == 'R') {
            label = 'Registrado'
        } else if (estatus == 'I') {
            label = 'Impreso'
        }
        return label
    }

    def generarDetalle() {
        Contrato contrato = Contrato.findById(params.id as Long)
        for (Integer i = 1; i <= 12; i++) {
            def fecha = contratoService.calcularFechaPago(i, contrato.fechaContrato)
            ContratoDetalle contratoDetalle = new ContratoDetalle()

            def costoMensualInteres = (contrato.montoReqAct * 5) / 100
            def costoMensualMonitoreo = (contrato.montoReqAct * 1) / 100 < 800 ? 800 : (contrato.montoReqAct * 1) / 100
            def costoMensualGPS = (contrato.montoReqAct * 0.75) / 100 < 600 ? 600 : (contrato.montoReqAct * 0.75) / 100

            contratoDetalle.contrato = contrato
            contratoDetalle.parcialidad = i
            contratoDetalle.fecha = fecha
            contratoDetalle.interes = costoMensualInteres
            contratoDetalle.monitoreo = costoMensualMonitoreo
            contratoDetalle.gps = costoMensualGPS
            contratoDetalle.capital = i == 12 ? contrato.montoReqAct : 0
            contratoDetalle.subtotal = i == 12 ? (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS + contrato.montoReqAct) : (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS)
            contratoDetalle.iva = (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS) * 0.16
            contratoDetalle.saldoFinal = i == 12 ? 0 : contrato.montoReqAct
            contratoDetalle.estatus = 'P'
            contratoDetalle.conciliado = false
            contratoDetalle.save(flush: true, failOnError: true)
        }
        respond message: 'ok'
    }

    def actualizaInicioFin() {
        def contratos = CargadeContratos.findAllByParcialidadActualIsNotNull()
        for (contrato in contratos) {
            if (contrato.parcialidadActual >= 1 && contrato.parcialidadActual <= 12) {
                contrato.inicio = 1
                contrato.fin = 12
            } else if (contrato.parcialidadActual >= 13 && contrato.parcialidadActual <= 24) {
                contrato.inicio = 13
                contrato.fin = 24
            } else if (contrato.parcialidadActual >= 25 && contrato.parcialidadActual <= 36) {
                contrato.inicio = 25
                contrato.fin = 36
            } else if (contrato.parcialidadActual >= 37 && contrato.parcialidadActual <= 48) {
                contrato.inicio = 37
                contrato.fin = 48
            }
            contrato.cargado = false
            contrato.save(flush: true, failOnError: true)
        }
        respond message: 'ok'
    }

    def eliminar() {
        def carga = CargadeContratos.list()
        def contratos = Contrato.findAllByFolioCargaIsNotNull()
        respond message: 'ok'
    }

    @Transactional
    def generacionDetalles() {
        def contratos = Contrato.findAllByActualizado(false)
        for (contrato in contratos) {
            for (Integer i = contrato.inicioReq; i <= contrato.finReq; i++) {
                def fecha = contratoService.calcularFechaPago(i, contrato.fechaContrato)
                ContratoDetalle contratoDetalle = new ContratoDetalle()

                def costoMensualInteres = (contrato.montoReqAct * 5) / 100
                def costoMensualMonitoreo = (contrato.montoReqAct * 1) / 100 < 800 ? 800 : (contrato.montoReqAct * 1) / 100
                def costoMensualGPS = (contrato.montoReqAct * 0.75) / 100 < 600 ? 600 : (contrato.montoReqAct * 0.75) / 100

                contratoDetalle.contrato = contrato
                contratoDetalle.parcialidad = i
                contratoDetalle.fecha = fecha
                contratoDetalle.interes = costoMensualInteres
                contratoDetalle.monitoreo = costoMensualMonitoreo
                contratoDetalle.gps = costoMensualGPS
                contratoDetalle.capital = i == 12 ? contrato.montoReqAct : 0
                contratoDetalle.subtotal = i == 12 ? (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS + contrato.montoReqAct) : (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS)
                contratoDetalle.iva = (costoMensualInteres + costoMensualMonitoreo + costoMensualGPS) * 0.16
                contratoDetalle.saldoFinal = i == 12 ? 0 : contrato.montoReqAct
                contratoDetalle.estatus = 'P'
                contratoDetalle.conciliado = false
                contratoDetalle.save(flush: true, failOnError: true)
            }
            contrato.actualizado = true
            contrato.save(flush: true, failOnError: true)

            CargadeContratos cargadeContratos = CargadeContratos.findByNumeroContrato(contrato.numeroContrato)
            if (cargadeContratos!= null){
                cargadeContratos.cargado = true
                cargadeContratos.save(flush: true, failOnError: true)
            }

        }
        respond message: 'ok'
    }

    @Transactional
    def ajuste() {
        def listaExcuidos = [
                '2242', '2232', 'MTY4', '2236', '2134', '2218', '1596', '1843', '1946', '1917', '2133', '2101', '1953',
                '2271', '1722', '2119', '1643', '2220', '1995', '1741', '1268', '2049', '2074', '1283', '1110', '1754',
                '2203', 'MTY1', '1419', '1643', '1972', '1981', '2207', '2214', 'MTY4', 'MTY12', '676', '682', '844',
                '915', '952', '1013', '1060', '1061', '1122', '1203', '1254', '1259', '1312', '1361', '1363', '1418',
                '1420', '1484', '1578', '1718', '1724', '1762', '1831', '1833', '1908', '1978', '2041', '2044', '2104',
                '2105', '2107', '2155', '2157', '2159', 'MTY5', '2156', '1555', '2072', '1945', '1942', '2015', '1741',
                '2125', '2144', '2218', '1050', '358', '120', '1907', '907', '1748', '1638', '2026', '524', '1411',
                '2042', '787', '1835', '356', '2103', '528', '1200', '1422', '523', '1180', '1580', '1068', '910',
                '130', '388', '1359', '363', '600', '2194', '1742', '2156', '1645', '2032', '863', '1515', '1955',
                '1405', '1530', '905', '1786', '1065', '1970', '136', '2138', '2193', '2237', '1632', '2127', '1728',
                '1943', '1983', '1595', '2145', '1014', 'MTY8', '2255', '2177', '764', '2069', '1221', '684', '1281',
                '1294', '1575', '1827', '1429', '1838', '1018', '1224', '1424', '1256', '1720', '1087', '1056', '2008',
                '1344', '2039', '1388', '1717', '1658', '1826', '1714', '1436', '1681', '1914', '2017', '1469', '1839',
                '1735', '1371', '24', '1783', 'MTY8'
        ]

        def ex = Contrato.findAllByNumeroContratoInList(listaExcuidos)

//        def contratos = Contrato.findAllByIdBetweenAndTipoFolioAndNumeroContratoNotInList(1, 2135, 'CDMX',listaExcuidos)
//        def contratos = Contrato.findAllByIdBetweenAndTipoFolioAndNumeroContratoNotInList(1, 2135, 'CDMX',listaExcuidos)
        def contratoDetalles = ContratoDetalle.findAllByFechaLessThanEqualsAndContratoNotInList(sdf.parse('2022-04-18'), ex)
        for (cd in contratoDetalles){
            cd.conciliado = true
            cd.save(flush: true, failOnError: true)
        }
        respond me: 'ok'
    }

    def getClave(String tipoFolio) {
        String cveTipo = ''
        switch (tipoFolio) {
            case 'CDMX':
                cveTipo = 'CONTRATO'
                break
            case 'GDL':
                cveTipo = 'CONTRATOGUADALAJARA'
                break
            case 'MTY':
                cveTipo = 'CONTRATOMONTERREY'
                break
            case 'P':
                cveTipo = 'CONTRATOPRUEBAS'
                break
        }
        return cveTipo
    }

    def obtenerFolio(String clave) {
        Folios folio = Folios.findByCveTipo(getClave(clave))
        def folioRecuperado = getFolioRecuperado(getClave(clave))
        String f = folio != null ? (folio.folio + 1).toString() : '1'
        String folioContrato = folioRecuperado > 0 ? folioRecuperado : f
        respond folio: contratoFolio(folioContrato, clave)
    }

    def consulta() {
        def ids = []
        def lista = ConciliacionesDetalles.list().collect({
            [id: new Long(it.folioOperacion)]
        }).unique()

        for (li in lista){
            def cons = ContratoDetalle.findAllById(li.id)
            if (cons == null) {
                ids.push(li)
            }
        }

        respond ids
    }

    def ajusteConciliacion() {
        def cd = ConciliacionesDetalles.list()
        for (c in cd){
            ContratoDetalle detalle =  ContratoDetalle.findById(c.folioOperacion as Long)
            if (detalle!=null) {
                detalle.conciliado = true
                detalle.save(flush:true, failOnError: true)
            }
            LiquidacionBanco lb = LiquidacionBanco.findById(c.movimiento.id)
            if (lb!=null) {
                lb.conciliado = true
                lb.save(flush:true, failOnError: true)
            }
        }

        respond m: 'listo'
    }
}
