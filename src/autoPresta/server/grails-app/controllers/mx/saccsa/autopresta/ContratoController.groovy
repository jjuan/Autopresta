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
                    numeroContrato               : it.numeroContrato != '' ? contratoFolio(it.numeroContrato, it.contratoPrueba, it.contratoMonterrey) : '',
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
                    numeroContrato               : it.numeroContrato != '' ? contratoFolio(it.numeroContrato, it.contratoPrueba, it.contratoMonterrey) : '',
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
        } else if (contrato.contratoMonterrey) {
            def folio = getFolioRecuperado('CONTRATOMONTERREY')
            if (folio == 0) {
                contrato.numeroContrato = folioService.generaFolio('CONTRATOMONTERREY').toString()
            } else {
                contrato.numeroContrato = contratoFolio(folio.toString(), false, true)
                FoliosRecuperados foliosRecuperado = FoliosRecuperados.findByCveTipoAndFolio('CONTRATOMONTERREY', folio.toString())
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
        Folios contratoMonterrey = Folios.findByCveTipo('CONTRATOMONTERREY')
        Folios contratoPrueba = Folios.findByCveTipo('ContratoPruebas')

        def folioRecuperadoP = getFolioRecuperado('ContratoPruebas')
        def folioRecuperado = getFolioRecuperado('Contrato')
        def folioRecuperadoMonterrey = getFolioRecuperado('CONTRATOMONTERREY')


        String folio = contrato != null ? (contrato.folio + 1).toString() : '1'
        String folioMonterrey = contratoMonterrey != null ? (contratoMonterrey.folio + 1).toString() : 'MTY0001'
        String folioPrueba = contratoPrueba != null ? (contratoPrueba.folio + 1).toString() + 'P' : '1P'
        respond(
                folio: folioRecuperado > 0 ? folioRecuperado : folio,
                folioMty: folioRecuperadoMonterrey > 0 ? folioRecuperadoMonterrey : folioMonterrey,
                folioPrueba: folioRecuperadoP > 0 ? folioRecuperadoP + 'P' : folioPrueba
        )
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
            if (contrato.contratoMonterrey == true) {
                foliosRecuperados.cveTipo = 'ContratoMonterrey'
            }
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

    String contratoFolio(String folio, Boolean contratoPrueba, Boolean contratoMonterrey) {
        if (contratoPrueba) {
            while (folio.length() < 5) {
                folio = '0' + folio
            }
        } else {
            while (folio.length() < 4) {
                folio = '0' + folio
            }

            if (contratoMonterrey) {
                folio = 'MTY' + folio
            }
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
}
