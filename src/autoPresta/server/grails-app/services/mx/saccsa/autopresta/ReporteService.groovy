package mx.saccsa.autopresta

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import mx.saccsa.common.Parametros

import java.text.NumberFormat
import java.text.SimpleDateFormat

@Transactional
class ReporteService {
    def springSecurityService
    def sdf = new SimpleDateFormat('yyyy-MM-dd')
    def year = new SimpleDateFormat('yyyy')
    def utilService
    def dateUtilService

    def colecion(Long id) {
        def contrato = Contrato.findById(id)
        def listaDatos = []
        def montoTotal = 0
        def pagos = ContratoDetalle.findAllByContrato(contrato)
        for (pago in pagos) {
            montoTotal = montoTotal + (pago.subtotal + pago.iva)
        }
        String clabe = Parametros.getValorByParametro('CLABE_BANCO')
        ContratoDetalle.findAllByContrato(contrato).eachWithIndex { ContratoDetalle it, int i ->
            listaDatos.add index: i + 1,
                    fechaPago: it.fecha,
                    interes: it.interes,
                    monitoreo: it.monitoreo,
                    gps: it.gps,
                    capital: it.capital,
                    totalAP: it.subtotal,
                    iva: it.iva,
                    totalCiva: (it.subtotal + it.iva),
                    saldoFinal: it.saldoFinal


        }
        def dir = Direccion.findByContratoAndPrincipal(contrato, true)
        String exterior = dir.exterior != null ? dir.exterior : 'S/N'
        def desempenio = ContratoDetalle.findByContratoAndParcialidad(contrato, contrato.tipoContrato.duracion.toString())
        def fechaFiniquito = ContratoDetalle.findByContratoAndParcialidad(contrato, '12')
        def prestamoSobreAvaluo = ((contrato.montoRequerido / contrato.valorDeCompra) * 100)
        def lista
        if (contrato.regimenFiscal.clave == 'PM') {
            lista = [
                    listaDatos                   : listaDatos,
                    nombre                       : contrato.nombres.toUpperCase(),
                    nombreRazonSocial            : contrato.razonesSociales.razonSocial.toUpperCase(),
                    rfcRazonSocial               : contrato.razonesSociales.rfc.toUpperCase(),
                    apellidos                    : campo(contrato.primerApellido) + ' ' + campo(contrato.segundoApellido),
                    rfc                          : contrato.rfc.toUpperCase(),
                    edad                         : contrato.edad.toString(),
                    whatsapp                     : campo(contrato.telefonoCelular),
                    telTrabajoCasa               : campo(contrato.telefonoOficina) + ' ' + campo(contrato.telefonoFijo),
                    noIdentificacionOficial      : contrato.claveElector != null ? contrato.claveElector.toUpperCase() : '',
                    calleNoExterior              : campo(dir.direccionPrincipal) + ' ' + exterior,
                    noInterior                   : dir.interior != null ? dir.interior : '',
                    colonia                      : dir.colonia != null ? dir.colonia.toUpperCase() : '',
                    codigoPostal                 : dir.cp != null ? dir.cp : '',
                    alcaldia                     : dir.municipio != null ? dir.municipio.toUpperCase() : '',
                    noContrato                   : contratoFolio(contrato),
                    montoPrestamo                : contrato.montoTransferencia,
                    montoTotalPagar              : montoTotal,
                    referenciaBancaria           : contrato.referencia ? contrato.referencia.toUpperCase() : '',
                    clabe                        : clabe,
                    plazo                        : '12 MESES',
                    desempeño                    : fecha(desempenio.fecha),
                    caracteristicas              : descripcion(contrato),
                    noDeVin                      : campo(contrato.numeroVin),
                    avaluo                       : contrato.valorDeCompra,
                    prestamo                     : contrato.montoRequerido,
                    prestamoSobreAvaluo          : prestamoSobreAvaluo.intValue() + '%',
                    montoPrestamoLetra           : numeroLetra(contrato.montoRequerido),
                    montoAvaluoLetra             : numeroLetra(contrato.valorDeCompra),
                    porcentajePrestamoSobreAvaluo: '(' + utilService.montoLetra(prestamoSobreAvaluo.intValue()) + ' PORCIENTO) ' + prestamoSobreAvaluo.intValue() + '%',
                    fechaLimiteFiniquito         : fecha(fechaFiniquito.fecha),
                    fecha                        : 'CDMX a ' + fecha(contrato.fechaContrato),
                    notasDescuentos              : contrato.detalleDescuentos.toUpperCase(),
                    acuerdo                      : acuerdo(contrato),
                    anio                         : year.format(contrato.fechaContrato),
                    nombres                      : contrato.razonesSociales ? contrato.razonesSociales.descLabel : contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
            ]
        } else if (contrato.nombresCoacreditado != null && contrato.regimenFiscal.clave != 'PM') {
            lista = [
                    listaDatos                         : listaDatos,
                    nombre                             : contrato.nombres.toUpperCase(),
                    apellidos                          : campo(contrato.primerApellido) + ' ' + campo(contrato.segundoApellido),
                    rfc                                : contrato.rfc.toUpperCase(),
                    edad                               : contrato.edad.toString(),
                    whatsapp                           : campo(contrato.telefonoCelular),
                    telTrabajoCasa                     : campo(contrato.telefonoOficina) + ' ' + campo(contrato.telefonoFijo),
                    noIdentificacionOficial            : contrato.claveElector != null ? contrato.claveElector.toUpperCase() : '',
                    nombreCoacreditado                 : contrato.nombresCoacreditado.toUpperCase(),
                    apellidosCoacreditado              : campo(contrato.primerApellidoCoacreditado) + ' ' + campo(contrato.segundoApellidoCoacreditado),
                    rfcCoacreditado                    : contrato.rfcCoacreditado.toUpperCase(),
                    edadCoacreditado                   : contrato.edadCoacreditado.toString(),
                    whatsappCoacreditado               : campo(contrato.telefonoCelularCoacreditado),
                    telTrabajoCasaCoacreditado         : campo(contrato.telefonoOficinaCoacreditado) + ' ' + campo(contrato.telefonoFijoCoacreditado),
                    noIdentificacionOficialCoacreditado: contrato.claveElectorCoacreditado != null ? contrato.claveElectorCoacreditado : '',
                    calleNoExterior                    : campo(dir.direccionPrincipal) + ' ' + exterior,
                    noInterior                         : dir.interior != null ? dir.interior : '',
                    colonia                            : dir.colonia != null ? dir.colonia.toUpperCase() : '',
                    codigoPostal                       : dir.cp != null ? dir.cp : '',
                    alcaldia                           : dir.municipio != null ? dir.municipio.toUpperCase() : '',
                    noContrato                         : contratoFolio(contrato),
                    montoPrestamo                      : contrato.montoTransferencia,
                    montoTotalPagar                    : montoTotal,
                    referenciaBancaria                 : contrato.referencia ? contrato.referencia.toUpperCase() : '',
                    clabe                              : clabe,
                    plazo                              : '12 MESES',
                    desempeño                          : fecha(desempenio.fecha),
                    caracteristicas                    : descripcion(contrato),
                    noDeVin                            : campo(contrato.numeroVin),
                    avaluo                             : contrato.valorDeCompra,
                    prestamo                           : contrato.montoRequerido,
                    prestamoSobreAvaluo                : prestamoSobreAvaluo.intValue() + '%',
                    montoPrestamoLetra                 : numeroLetra(contrato.montoRequerido),
                    montoAvaluoLetra                   : numeroLetra(contrato.valorDeCompra),
                    porcentajePrestamoSobreAvaluo      : '(' + utilService.montoLetra(prestamoSobreAvaluo.intValue()) + ' PORCIENTO) ' + prestamoSobreAvaluo.intValue() + '%',
                    fechaLimiteFiniquito               : fecha(fechaFiniquito.fecha),
                    fecha                              : 'CDMX a ' + fecha(contrato.fechaContrato),
                    notasDescuentos                    : contrato.detalleDescuentos.toUpperCase(),
                    acuerdo                            : acuerdo(contrato),
                    anio                               : year.format(contrato.fechaContrato),
                    nombres                            : contrato.razonesSociales ? contrato.razonesSociales.descLabel : contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido,
                    acuerdoCoacreditado                : acuerdoCoacreditado(contrato),
                    nombresCoacreditado                : contrato.nombresCoacreditado + ' ' + contrato.primerApellidoCoacreditado + ' ' + contrato.segundoApellidoCoacreditado
            ]
        } else {
            lista = [
                    listaDatos                   : listaDatos,
                    nombre                       : contrato.nombres.toUpperCase(),
                    apellidos                    : campo(contrato.primerApellido) + ' ' + campo(contrato.segundoApellido),
                    rfc                          : contrato.rfc.toUpperCase(),
                    edad                         : contrato.edad.toString(),
                    whatsapp                     : campo(contrato.telefonoCelular),
                    telTrabajoCasa               : campo(contrato.telefonoOficina) + ' ' + campo(contrato.telefonoFijo),
                    noIdentificacionOficial      : contrato.claveElector != null ? contrato.claveElector.toUpperCase() : '',
                    calleNoExterior              : campo(dir.direccionPrincipal) + ' ' + exterior,
                    noInterior                   : dir.interior != null ? dir.interior : '',
                    colonia                      : dir.colonia != null ? dir.colonia.toUpperCase() : '',
                    codigoPostal                 : dir.cp != null ? dir.cp : '',
                    alcaldia                     : dir.municipio != null ? dir.municipio.toUpperCase() : '',
                    noContrato                   : contratoFolio(contrato),
                    montoPrestamo                : contrato.montoTransferencia,
                    montoTotalPagar              : montoTotal,
                    referenciaBancaria           : contrato.referencia ? contrato.referencia.toUpperCase() : '',
                    clabe                        : clabe,
                    plazo                        : '12 MESES',
                    desempeño                    : fecha(desempenio.fecha),
                    caracteristicas              : descripcion(contrato),
                    noDeVin                      : campo(contrato.numeroVin),
                    avaluo                       : contrato.valorDeCompra,
                    prestamo                     : contrato.montoRequerido,
                    prestamoSobreAvaluo          : prestamoSobreAvaluo.intValue() + '%',
                    montoPrestamoLetra           : numeroLetra(contrato.montoRequerido),
                    montoAvaluoLetra             : numeroLetra(contrato.valorDeCompra),
                    porcentajePrestamoSobreAvaluo: '(' + utilService.montoLetra(prestamoSobreAvaluo.intValue()) + ' PORCIENTO) ' + prestamoSobreAvaluo.intValue() + '%',
                    fechaLimiteFiniquito         : fecha(fechaFiniquito.fecha),
                    fecha                        : 'CDMX a ' + fecha(contrato.fechaContrato),
                    notasDescuentos              : contrato.detalleDescuentos.toUpperCase(),
                    acuerdo                      : acuerdo(contrato),
                    anio                         : year.format(contrato.fechaContrato),
                    nombres                      : contrato.razonesSociales ? contrato.razonesSociales.descLabel : contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
            ]
        }

        return lista
    }

    String descripcion(Contrato contrato) {
        String desc = contrato.marca.nombre.toUpperCase() + ' ' + contrato.modelo.nombre.toUpperCase() + ' AÑO ' + contrato.anio + ', COLOR ' + contrato.color.toUpperCase() + ', PLACAS ' + contrato.placas.toUpperCase()
        return desc
    }

    String campo(def s) {
        if (s == null) {
            return ''
        } else {
            return s.toUpperCase()
        }
    }

    def fecha(Date f) {
        Calendar c = Calendar.getInstance()
        String mes = ''
        c.setTime(f)
        Long month = c.get(Calendar.MONTH) + 1
        switch (month) {
            case 1:
                mes = 'Enero'
                break;
            case 2:
                mes = 'Febrero'
                break;
            case 3:
                mes = 'Marzo'
                break;
            case 4:
                mes = 'Abril'
                break;
            case 5:
                mes = 'Mayo'
                break;
            case 6:
                mes = 'Junio'
                break;
            case 7:
                mes = 'Julio'
                break;
            case 8:
                mes = 'Agosto'
                break;
            case 9:
                mes = 'Septiembre'
                break;
            case 10:
                mes = 'Octubre'
                break;
            case 11:
                mes = 'Noviembre'
                break;
            case 12:
                mes = 'Diciembre'
                break;
        }
        return dateUtilService.diaSemana(f) + ', ' + c.get(Calendar.DAY_OF_MONTH) + ' de ' + mes + ' de ' + c.get(Calendar.YEAR)
    }

    def fechaCorta(Date f) {
        Calendar c = Calendar.getInstance()
        String mes = ''
        c.setTime(f)
        Long month = c.get(Calendar.MONTH) + 1
        switch (month) {
            case 1:
                mes = 'Enero'
                break;
            case 2:
                mes = 'Febrero'
                break;
            case 3:
                mes = 'Marzo'
                break;
            case 4:
                mes = 'Abril'
                break;
            case 5:
                mes = 'Mayo'
                break;
            case 6:
                mes = 'Junio'
                break;
            case 7:
                mes = 'Julio'
                break;
            case 8:
                mes = 'Agosto'
                break;
            case 9:
                mes = 'Septiembre'
                break;
            case 10:
                mes = 'Octubre'
                break;
            case 11:
                mes = 'Noviembre'
                break;
            case 12:
                mes = 'Diciembre'
                break;
        }
        return c.get(Calendar.DAY_OF_MONTH) + ' de ' + mes + ' de ' + c.get(Calendar.YEAR)
    }

    String contratoFolio(Contrato contrato) {
        String folio = contrato.numeroContrato
        if (contrato.contratoPrueba) {
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

    def numeroLetra(BigDecimal monto) {
        Locale usa = new Locale("en", "US")
        Currency dollars = Currency.getInstance(usa)
        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa);

        String numero = dollarFormat.format(monto)
        String letra = utilService.cantidadLetra(monto, Divisas.findByClave('MXN'))
        return " " + numero + " " + letra
    }

    def acuerdo(Contrato contrato) {
        String nombres = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
        String documento = IdentificacionesOficiales.getNombreById(contrato.documentoOficial)
        String documentoValor = contrato.claveElector
        Direccion direccion = Direccion.findByContratoAndPrincipal(contrato, true)

        return "EL SUSCRITO " + nombres.toUpperCase() + " IDENTIFICÁNDOME CON " + documento.toUpperCase() + " " + documentoValor.toUpperCase() +
                " SEÑALANDO DOMICILIO EN " + direccion.direccionPrincipal.toUpperCase() + " " + direccion.exterior + ", COL " + direccion.colonia.toUpperCase() + ", CP " + direccion.cp + ", " + direccion.municipio.toUpperCase() + ", " + direccion.entidad.toUpperCase() + " POR MEDIO DEL PRESENTE ESCRITO, EN ESTE ACTO HAGO ENTREGA FÍSICA, MATERIAL Y " +
                "VOLUNTARIA DEL VEHÍCULO QUE FUE DADO EN PRENDA EN EL CONTRATO DE PRÉSTAMO (MUTUO CON INTERÉS Y " +
                "GARANTÍA PRENDARIA) DE LA MARCA " + contrato.marca.nombre.toUpperCase() + " SUB MARCA " + contrato.modelo.nombre.toUpperCase() + " AÑO " + contrato.anio + " COLOR " + contrato.color + " NÚMERO DE SERIE " +
                contrato.numeroVin + " MOTOR " + contrato.numeroDeMotor + " PLACAS " + contrato.placas + " A SU PROPIETARIO AP SERVICIOS FINANCIEROS, S.A. DE " +
                "C.V. A QUIEN, TODA VEZ QUE SE INCURRA EN IMPAGO, SE LE DEBE RECONOCER COMO ÚNICO TITULAR, " +
                "OTORGANDO PLENA POSESIÓN DEL MISMO EN ESTE DOCUMENTO.\n" +
                "ASÍ MISMO, ME COMPROMETO A CUBRIR CUALQUIER GASTO POR CONCEPTO DE REPARACIÓN MECÁNICA REQUIERA " +
                "DICHO VEHÍCULO POR ALGÚN DESPERFECTO QUE LE HAYA CAUSADO EL SUSCRITO DURANTE EL TIEMPO DE USO."
    }

    def acuerdoCoacreditado(Contrato contrato) {
        String nombres = contrato.nombresCoacreditado + ' ' + contrato.primerApellidoCoacreditado + ' ' + contrato.segundoApellidoCoacreditado
        String nombresTitular = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
//        String documento = IdentificacionesOficiales.getNombreById(contrato.documentoOficialCoacreditado)
//        String documentoValor = contrato.claveElectorCoacreditado

//        return "EL SUSCRITO " + nombres.toUpperCase() + " IDENTIFICÁNDOME CON " + documento.toUpperCase() + " " + documentoValor.toUpperCase() +
//                " SEÑALANDO DOMICILIO EN " + direccion.direccionPrincipal.toUpperCase() + " " + direccion.exterior + ", COL " + direccion.colonia.toUpperCase() + ", CP " + direccion.cp + ", " + direccion.municipio.toUpperCase() + ", " + direccion.entidad.toUpperCase() + " POR MEDIO DEL PRESENTE ESCRITO, EN ESTE ACTO HAGO ENTREGA FÍSICA, MATERIAL Y " +
//                "VOLUNTARIA DEL VEHÍCULO QUE FUE DADO EN PRENDA EN EL CONTRATO DE PRÉSTAMO (MUTUO CON INTERÉS Y " +
//                "GARANTÍA PRENDARIA) DE LA MARCA " + contrato.marca.nombre.toUpperCase() + " SUB MARCA " + contrato.modelo.nombre.toUpperCase() + " AÑO " + contrato.anio + " COLOR " + contrato.color + " NÚMERO DE SERIE " +
//                contrato.numeroVin + " MOTOR " + contrato.numeroDeMotor + " PLACAS " + contrato.placas + " A SU PROPIETARIO AP SERVICIOS FINANCIEROS, S.A. DE " +
//                "C.V. A QUIEN, TODA VEZ QUE SE INCURRA EN IMPAGO, SE LE DEBE RECONOCER COMO ÚNICO TITULAR, " +
//                "OTORGANDO PLENA POSESIÓN DEL MISMO EN ESTE DOCUMENTO.\n" +
//                "ASÍ MISMO, ME COMPROMETO A CUBRIR CUALQUIER GASTO POR CONCEPTO DE REPARACIÓN MECÁNICA REQUIERA " +
//                "DICHO VEHÍCULO POR ALGÚN DESPERFECTO QUE LE HAYA CAUSADO EL SUSCRITO DURANTE EL TIEMPO DE USO."
        return "Se extiende el presente para informar que, YO <style isBold=\"true\">" + nombres.toUpperCase() + "</style> en la fecha en que se emite " +
                "este documento, acepto y reconozco el compromiso y total responsabilidad sobre el contrato <style isBold=\"true\">" +
                contratoFolio(contrato) + "</style> firmado el día <style isBold=\"true\">" + fechaCorta(contrato.fechaContrato) + "</style> a nombre de <style isBold=\"true\">" + nombresTitular + "</style>, " +
                "para realizar los pagos correspondientes de las mensualidades del préstamo, incluyendo los intereses y " +
                "en su caso los cargos moratorios, comisiones e I.V.A. bajo los términos de dicho contrato. " +
                "Todo lo anterior, aplicando las responsabilidades y obligaciones del contrato antes mencionado, y se pueda" +
                " ejercer de acuerdo con las cláusulas establecidas y sin deslindar de responsabilidad alguna al titular" +
                " original de dicho contrato. "
    }

    def contratosFirmados(Date fechaInicio, Date fechaFin) {
        def lista = Contrato.findAllByContratoPruebaAndEstatusAndFechaContratoBetween(false, 'F', fechaInicio, fechaFin).collect({
            [
                    numeroContrato: it.numeroContrato != '' ? contratoFolio(it) : '',
                    titular       : it.razonesSociales ? it.razonesSociales.descLabel : it.nombres + ' ' + it.primerApellido + ' ' + it.segundoApellido,
                    representante : it.razonesSociales ? it.nombres + ' ' + it.primerApellido + ' ' + it.segundoApellido : '',
                    fechaContrato : it?.fechaContrato,
                    montoRequerido: it?.montoRequerido,
                    total         : ContratoDetalle.executeQuery('Select sum(subtotal + iva) from ContratoDetalle where contrato =:contrato', [contrato: it])[0],
            ]
        })
        return lista
    }

    def pagosRealizados(Date fechaInicio, Date fechaFin) {
//        ContratoDetalle contratoDetalle = ContratoDetalle.findAllByFecha()
        def lista = ContratoDetalle.findAllByFechaBetween(fechaInicio, fechaFin).collect({
            [
                    parcialidad: it.parcialidad,
                    iva        : it.iva,
                    fechaRep   : it.fecha,
                    saldoFinal : it.saldoFinal,
                    subtotal   : it.subtotal,
                    capital    : it.capital,
                    monitoreo  : it.monitoreo,
                    contrato   : it.contrato.razonesSociales ? it.contrato.razonesSociales.descLabel.toUpperCase() : it.contrato.nombres.toUpperCase() + ' ' + it.contrato.primerApellido.toUpperCase() + ' ' + it.contrato.segundoApellido.toUpperCase(),
                    gps        : it.gps,
                    interes    : it.interes

            ]
        })

//        def name = contratoDetalle.contrato.nombres + ' ' + contratoDetalle.contrato.primerApellido + ' ' + contratoDetalle.contrato.segundoApellido
//
//        def datos = [
//                parcialidad:contratoDetalle.parcialidad,
//                iva:contratoDetalle.iva,
//                fecha:contratoDetalle.fecha,
//                saldoFinal: contratoDetalle.saldoFinal,
//                subtotal: contratoDetalle.subtotal,
//                capital: contratoDetalle.capital,
//                monitoreo: contratoDetalle.monitoreo,
//                contrato: name,
//                gps: contratoDetalle.gps,
//                interes: contratoDetalle.interes
//        ]
        return lista
    }
}
