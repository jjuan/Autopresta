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
    def conciliacionesService

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

        def lista = [
                nombreRazonSocial                  : contrato.razonesSociales ? contrato.razonesSociales.razonSocial.toUpperCase() : null,
                rfcRazonSocial                     : contrato.razonesSociales ? contrato.razonesSociales.rfc.toUpperCase() : null,

                nombreCoacreditado                 : contrato.nombresCoacreditado ? contrato.nombresCoacreditado.toUpperCase() : null,
                apellidosCoacreditado              : contrato.nombresCoacreditado ? campo(contrato.primerApellidoCoacreditado) + ' ' + campo(contrato.segundoApellidoCoacreditado) : null,
                rfcCoacreditado                    : contrato.nombresCoacreditado ? contrato.rfcCoacreditado.toUpperCase() : null,
                edadCoacreditado                   : contrato.nombresCoacreditado ? contrato.edadCoacreditado.toString() : null,
                whatsappCoacreditado               : contrato.nombresCoacreditado ? campo(contrato.telefonoCelularCoacreditado) : null,
                telTrabajoCasaCoacreditado         : contrato.nombresCoacreditado ? campo(contrato.telefonoOficinaCoacreditado) + ' ' + campo(contrato.telefonoFijoCoacreditado) : null,
                noIdentificacionOficialCoacreditado: contrato.nombresCoacreditado ? contrato.claveElectorCoacreditado != null ? contrato.claveElectorCoacreditado : '' : null,
                acuerdoCoacreditado                : contrato.nombresCoacreditado ? acuerdoCoacreditado(contrato) : null,
                nombresCoacreditado                : contrato.nombresCoacreditado ? contrato.nombresCoacreditado + ' ' + contrato.primerApellidoCoacreditado + ' ' + contrato.segundoApellidoCoacreditado : null,

                listaDatos                         : listaDatos,
                nombre                             : contrato.nombres.toUpperCase(),
                apellidos                          : campo(contrato.primerApellido) + ' ' + campo(contrato.segundoApellido),
                rfc                                : contrato.rfc.toUpperCase(),
                edad                               : contrato.edad.toString(),
                whatsapp                           : campo(contrato.telefonoCelular),
                telTrabajoCasa                     : campo(contrato.telefonoOficina) + ' ' + campo(contrato.telefonoFijo),
                noIdentificacionOficial            : contrato.claveElector != null ? contrato.claveElector.toUpperCase() : '',
                calleNoExterior                    : campo(dir.direccionPrincipal) + ' ' + exterior,
                noInterior                         : dir.interior != null ? dir.interior : '',
                colonia                            : dir.colonia != null ? dir.colonia.toUpperCase() : '',
                codigoPostal                       : dir.cp != null ? cpFormato(dir.cp) : '',
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
                pagoAnticipado                     : pagoAnticipado(contrato.tipoContrato.duracion),
                firmaLiquidacion                   : contrato.razonesSociales ? contrato.razonesSociales.descLabel : contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido,
                acuerdoLiquidacion                 : solicitudLiquidacion(contrato),
                duracion                           : contrato.tipoContrato.duracion.toString(),
                pagoMensual                        : (desempenio.subtotal + desempenio.iva),
                fechaContrato                      : contrato.fechaContrato
        ]

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
        c.setTime(f)
        def meses = [
                'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio',
                'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre']
        return dateUtilService.diaSemana(f) + ', ' + c.get(Calendar.DAY_OF_MONTH) + ' de ' + meses[c.get(Calendar.MONTH)] + ' de ' + c.get(Calendar.YEAR)
    }

    def fechaCorta(Date f) {
        Calendar c = Calendar.getInstance()
        c.setTime(f)
        def meses = [
                'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio',
                'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre']
        return c.get(Calendar.DAY_OF_MONTH) + ' de ' + meses[c.get(Calendar.MONTH)] + ' de ' + c.get(Calendar.YEAR)
    }

    String contratoFolio(Contrato contrato) {
        String folio = contrato.numeroContrato
            switch (contrato.tipoFolio) {
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

        return "EL SUSCRITO " + ftr(nombres.toUpperCase(), 'n') + " IDENTIFICÁNDOME CON " +
                documento.toUpperCase() + " " + ftr(documentoValor.toUpperCase(), 'n') + " SEÑALANDO DOMICILIO" +
                " EN " + ftr(direccion.direccionPrincipal.toUpperCase() + " " + direccion.exterior + ", COL " +
                direccion.colonia.toUpperCase() + ", CP " + cpFormato(direccion.cp) + ", " + direccion.municipio.toUpperCase() +
                ", " + direccion.entidad.toUpperCase(), 'n') + " POR MEDIO DEL PRESENTE ESCRITO, EN ESTE ACTO" +
                " HAGO ENTREGA FÍSICA, MATERIAL Y VOLUNTARIA DEL VEHÍCULO QUE FUE DADO EN PRENDA EN EL CONTRATO DE " +
                "PRÉSTAMO (MUTUO CON INTERÉS Y GARANTÍA PRENDARIA) DE LA MARCA " + ftr(contrato.marca.nombre.toUpperCase(),
                'n') + " SUB MARCA " + ftr(contrato.modelo.nombre.toUpperCase(), 'n') + " AÑO " +
                ftr(contrato.anio, 'n') + " COLOR " + ftr(contrato.color, 'n') + " NÚMERO DE SERIE " +
                ftr(contrato.numeroVin, 'n') + " MOTOR " + ftr(contrato.numeroDeMotor, 'n') + " PLACAS " +
                ftr(contrato.placas, 'n') + " A SU PROPIETARIO " + ftr("AP SERVICIOS FINANCIEROS, S.A. DE C.V.",
                'ns') + " A QUIEN, TODA VEZ QUE SE INCURRA EN IMPAGO, SE LE DEBE RECONOCER COMO ÚNICO TITULAR," +
                " OTORGANDO PLENA POSESIÓN DEL MISMO EN ESTE DOCUMENTO.\nASÍ MISMO, ME COMPROMETO A CUBRIR CUALQUIER GASTO POR " +
                "CONCEPTO DE REPARACIÓN MECÁNICA REQUIERA DICHO VEHÍCULO POR ALGÚN DESPERFECTO QUE LE HAYA CAUSADO EL" +
                " SUSCRITO DURANTE EL TIEMPO DE USO."
    }

    def acuerdoCoacreditado(Contrato contrato) {
        String nombres = contrato.nombresCoacreditado + ' ' + contrato.primerApellidoCoacreditado + ' ' + contrato.segundoApellidoCoacreditado
        String nombresTitular = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido

        return "Se extiende el presente para informar que, YO " + ftr(nombres.toUpperCase(), 'n') +
                " en la fecha en que se emite este documento, acepto y reconozco el compromiso y total responsabilidad" +
                " sobre el contrato " + ftr(contratoFolio(contrato), 'n') + " firmado el día " +
                ftr(fechaCorta(contrato.fechaContrato), 'n') + " a nombre de " +
                ftr(nombresTitular, 'n') + ", para realizar los pagos correspondientes de " +
                "las mensualidades del préstamo, incluyendo los intereses y en su caso los cargos moratorios, " +
                "comisiones e I.V.A. bajo los términos de dicho contrato. Todo lo anterior, aplicando las " +
                "responsabilidades y obligaciones del contrato antes mencionado, y se pueda ejercer de acuerdo con las" +
                " cláusulas establecidas y sin deslindar de responsabilidad alguna al titular original de dicho contrato. "
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

        return lista
    }

    def conciliaciones(Date fechaInicio, Date fechaFin) {
//        def lista = Conciliaciones.findAllByFechaConciliacionBetween(fechaInicio, fechaFin).collect({
//            [
//                    parcialidad: it.parcialidad,
//                    iva        : it.iva,
//                    fechaRep   : it.fecha,
//                    saldoFinal : it.saldoFinal,
//                    subtotal   : it.subtotal,
//                    capital    : it.capital,
//                    monitoreo  : it.monitoreo,
//                    contrato   : it.contrato.razonesSociales ? it.contrato.razonesSociales.descLabel.toUpperCase() : it.contrato.nombres.toUpperCase() + ' ' + it.contrato.primerApellido.toUpperCase() + ' ' + it.contrato.segundoApellido.toUpperCase(),
//                    gps        : it.gps,
//                    interes    : it.interes
//
//            ]
//        })

        def conciliaciones = Conciliaciones.findAllByFechaConciliacionBetween(fechaInicio, fechaFin)
        def lista = ConciliacionesDetalles.findAllByConciliacionesInList(conciliaciones).collect({
            [
                    titular           : conciliacionesService.getTitular(ContratoDetalle.findById(it.folioOperacion as Long)),
                    mensualidad       : ContratoDetalle.findById(it.folioOperacion as Long).parcialidad,
                    montoTotal      : ContratoDetalle.findById(it.folioOperacion as Long).subtotal + ContratoDetalle.findById(it.folioOperacion as Long).iva,
                    fechaPago         : ContratoDetalle.findById(it.folioOperacion as Long).fecha,
                    referenciaBancaria: it.movimiento.referencia,
                    fechaMovimiento   : it.movimiento.fecha,
                    contrato          : contratoFolio(ContratoDetalle.findById(it.folioOperacion as Long).contrato.numeroContrato, ContratoDetalle.findById(it.folioOperacion as Long).contrato.contratoPrueba, ContratoDetalle.findById(it.folioOperacion as Long).contrato.contratoMonterrey),
                    formaConciliacion : it.formaConciliacion,
                    montoMovimiento   : it.movimiento.monto
            ]
        })

        return lista
    }

    def cpFormato(Long cp) {
        String codigoPostal
        String cpArray = cp.toString()
        if (cpArray.length() == 4) {
            codigoPostal = '0' + cp.toString()
        } else {
            codigoPostal = cp.toString()
        }
        return codigoPostal
    }

    def pagoAnticipado(Long duracion) {
        String texto = ""
        String consumidor = ftr("“EL CONSUMIDOR”", 'n')
        String proveedor = ftr("“EL PROVEEDOR”", 'n')
        String incisoC = "\n\n" + consumidor + " podrá realizar pagos anticipados, siempre y cuando se encuentre al " +
                "corriente de sus pagos y haya cumplido el plazo de los 6 meses, por lo que los pagos anticipados se" +
                " aplicarán al saldo insoluto principal. " + consumidor + " deberá avisar por escrito 45 días naturales" +
                " a " + proveedor + " su deseo de realizar el 50% de aportación a su capital, por lo que " + proveedor +
                " deberá aplicar dicho pago anticipado al saldo insoluto."

        switch (duracion) {
            case 3:
                texto = "en la Carátula del presente Contrato, conforme a las opciones de pago descritas en éste, " +
                        "habiendo cumplido el plazo de los tres meses y se le cobra a " + consumidor + " una penalización" +
                        " de una mensualidad del préstamo incluyendo los intereses y en su caso los cargos moratorios, " +
                        "Comisiones e I.V.A., con un aviso anticipado de 45 días naturales. Si avisa con 30 días naturales" +
                        " de anticipación, se le aplica a " + consumidor + " una penalización de una mensualidad y media" +
                        " del préstamo incluyendo los intereses y en su caso los cargos moratorios, Comisiones e I.V.A.," +
                        " y si avisa con 15 días naturales de anticipación, se le aplica a " + consumidor + " una " +
                        "penalización de dos mensualidades del préstamo incluyendo los intereses y en su caso los cargos" +
                        " moratorios, Comisiones e I.V.A., y en caso de no dar aviso anticipado, se le aplica a " +
                        consumidor + " la penalización de dos mensualidades y media del préstamo incluyendo los intereses" +
                        " y en su caso los cargos moratorios, Comisiones e I.V.A.. en cuyo caso " + consumidor + " deberá" +
                        " presentarse en el establecimiento. Efectuado el pago se procederá a la devolución de la Prenda" +
                        " en el acto. En caso de que " + consumidor + " no cumpla con el pago en el plazo establecido de" +
                        " los 3 meses, se extenderá en automático el plazo del pago anticipado a la mensualidad subsecuente" +
                        " de la 6ta mensualidad habiendo hecho el aviso anticipado de 45 días naturales. "
                break
            default:
                texto = "en la Carátula del presente Contrato, conforme a las opciones de pago descritas en éste, " +
                        "habiendo cumplido el pazo de los seis meses , y si avisa con 45 días naturales de anticipación," +
                        " se libera a " + consumidor + "de las penalizaciones del préstamo incluyendo los intereses y en " +
                        "su caso los cargos moratorios, Comisiones e I.V.A., si avisa con 30 días naturales de anticipación," +
                        " se le aplica a " + consumidor + " una penalización de una mensualidad del préstamo incluyendo " +
                        "los intereses y en su caso los cargos oratorios, Comisiones e I.V.A., y si avisa con 15 días " +
                        "naturales de anticipación, se le aplica a " + consumidor + " una penalización de una mensualidad" +
                        " y media del préstamo incluyendo los intereses y en su caso los cargos moratorios, Comisiones e " +
                        "I.V.A., y en caso de no dar aviso anticipado, se le aplica a " + consumidor + " la penalización" +
                        " de dos mensualidades del préstamo incluyendo los intereses y en su caso los cargos moratorios," +
                        " Comisiones e I.V.A. en cuyo caso " + consumidor + "deberá presentarse en el establecimiento " +
                        "efectuado el pago se procederá a la devolución de la Prenda en el acto.En caso de que " +
                        consumidor + " no cumpla con el pago anticipado en el plazo establecido de los 6 meses, se" +
                        " extenderá en automático el plazo del pago anticipado a la mensualidad subsecuente a la fecha" +
                        " del aviso anticipado de 45 días naturales."

                break
        }
        return texto + incisoC
    }

    def solicitudLiquidacion(Contrato contrato) {
        String texto
        String nombres = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
        ContratoDetalle contratoDetalle = ContratoDetalle.findByContratoAndParcialidad(contrato, contrato.tipoContrato.duracion.toString())
        if (contrato.razonesSociales != null) {
            texto = "Por medio del presente documento, YO " + ftr(nombres, 'n') + " en representación legal" +
                    " de " + ftr(contrato.razonesSociales.descLabel, 'n') + " en la fecha en que se emite " +
                    "este documento, solicito a " + ftr("AP SERVICIOS FINANCIEROS S.A. DE C.V.", 'n') +
                    " el término del contrato " + ftr(contratoFolio(contrato), 'n') + " celebrado el " +
                    ftr(fecha(contrato.fechaContrato), 'n') + ", y conforme a las cláusulas que se especifican" +
                    " en el mismo, acepto y reconozco realizar el pago total de mi adeudo el día " +
                    ftr(fecha(contratoDetalle.fecha), 'n') + " como fecha límite de pago " +
                    verificarHabil(contrato) + "de acuerdo al calendario de pagos especificado en la carátula del " +
                    "contrato antes mencionado. \nDe no ser así, me comprometo a pagar el 10% del adeudo total por cada" +
                    " día de atraso.\n\n" + ftr("ES NECESARIO QUE LAS MENSUALIDADES ANTES DE LA FECHA DE " +
                    "LIQUIDACIÓN DEL ADEUDO ENCUENTREN CUBIERTAS EN TIEMPO, EL IMPORTE PUEDE INCREMENTAR EN CASO DE" +
                    " INCUMPLIR CON LAS FECHAS ESTIPULADAS", 'n')
        } else {
            texto = "Por medio del presente documento, YO " + ftr(nombres, 'n') + " en la fecha en que se " +
                    "emite este documento, solicito a " + ftr("AP SERVICIOS FINANCIEROS S.A. DE C.V.", 'n') +
                    " el término del contrato " + ftr(contratoFolio(contrato), 'n') + " celebrado el " +
                    ftr(fecha(contrato.fechaContrato), 'n') + ", y conforme a las cláusulas que se especifican" +
                    " en el mismo, acepto y reconozco realizar el pago total de mi adeudo el día " +
                    ftr(fecha(contratoDetalle.fecha), 'n') + " como fecha límite de pago " + verificarHabil(contrato) +
                    "de acuerdo al calendario de pagos especificado en la carátula del contrato antes mencionado. \nDe" +
                    " no ser así, me comprometo a pagar el 10% del adeudo total por cada día de atraso.\n\n" +
                    ftr("ES NECESARIO QUE LAS MENSUALIDADES ANTES DE LA FECHA DE LIQUIDACIÓN DEL ADEUDO ENCUENTREN" +
                            " CUBIERTAS EN TIEMPO, EL IMPORTE PUEDE INCREMENTAR EN CASO DE INCUMPLIR CON LAS FECHAS ESTIPULADAS\n", 'n')
        }
        return texto
    }

    def ftr(String texto, String tipoFormato) {
        String formato = "<style "
        switch (tipoFormato) {
            case 'n':
                formato = formato + "isBold=\"true\">"
                break
            case 's':
                formato = formato + "isUnderline=\"true\">"
                break
            case 'ns':
                formato = formato + "isBold=\"true\" isUnderline=\"true\">"
                break
        }
        return formato + texto + "</style>"
    }

    def verificarHabil(Contrato contrato) {
        SimpleDateFormat dia = new SimpleDateFormat('dd')
        def fechaContrato = dia.format(contrato.fechaContrato)
        def fechaLiquidacion = dia.format(ContratoDetalle.findByContratoAndParcialidad(contrato, contrato.tipoContrato.duracion.toString()).fecha)
        return fechaContrato == fechaLiquidacion ? '' : '(al tratarse de un dia inhábil) '
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
}
