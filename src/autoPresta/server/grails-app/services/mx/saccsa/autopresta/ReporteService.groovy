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
                noContrato                         : contratoFolio(contrato.numeroContrato, contrato.contratoPrueba, contrato.contratoMonterrey),
                montoPrestamo                      : contrato.montoTransferencia,
                montoTotalPagar                    : montoTotal,
                referenciaBancaria                 : contrato.referencia ? contrato.referencia.toUpperCase() : '',
                clabe                              : clabe,
                plazo                              : '12 MESES',
                desempe??o                          : fecha(desempenio.fecha),
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
        String desc = contrato.marca.nombre.toUpperCase() + ' ' + contrato.modelo.nombre.toUpperCase() + ' A??O ' + contrato.anio + ', COLOR ' + contrato.color.toUpperCase() + ', PLACAS ' + contrato.placas.toUpperCase()
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

        return "EL SUSCRITO " + ftr(nombres.toUpperCase(), 'n') + " IDENTIFIC??NDOME CON " +
                documento.toUpperCase() + " " + ftr(documentoValor.toUpperCase(), 'n') + " SE??ALANDO DOMICILIO" +
                " EN " + ftr(direccion.direccionPrincipal.toUpperCase() + " " + direccion.exterior + ", COL " +
                direccion.colonia.toUpperCase() + ", CP " + cpFormato(direccion.cp) + ", " + direccion.municipio.toUpperCase() +
                ", " + direccion.entidad.toUpperCase(), 'n') + " POR MEDIO DEL PRESENTE ESCRITO, EN ESTE ACTO" +
                " HAGO ENTREGA F??SICA, MATERIAL Y VOLUNTARIA DEL VEH??CULO QUE FUE DADO EN PRENDA EN EL CONTRATO DE " +
                "PR??STAMO (MUTUO CON INTER??S Y GARANT??A PRENDARIA) DE LA MARCA " + ftr(contrato.marca.nombre.toUpperCase(),
                'n') + " SUB MARCA " + ftr(contrato.modelo.nombre.toUpperCase(), 'n') + " A??O " +
                ftr(contrato.anio, 'n') + " COLOR " + ftr(contrato.color, 'n') + " N??MERO DE SERIE " +
                ftr(contrato.numeroVin, 'n') + " MOTOR " + ftr(contrato.numeroDeMotor, 'n') + " PLACAS " +
                ftr(contrato.placas, 'n') + " A SU PROPIETARIO " + ftr("AP SERVICIOS FINANCIEROS, S.A. DE C.V.",
                'ns') + " A QUIEN, TODA VEZ QUE SE INCURRA EN IMPAGO, SE LE DEBE RECONOCER COMO ??NICO TITULAR," +
                " OTORGANDO PLENA POSESI??N DEL MISMO EN ESTE DOCUMENTO.\nAS?? MISMO, ME COMPROMETO A CUBRIR CUALQUIER GASTO POR " +
                "CONCEPTO DE REPARACI??N MEC??NICA REQUIERA DICHO VEH??CULO POR ALG??N DESPERFECTO QUE LE HAYA CAUSADO EL" +
                " SUSCRITO DURANTE EL TIEMPO DE USO."
    }

    def acuerdoCoacreditado(Contrato contrato) {
        String nombres = contrato.nombresCoacreditado + ' ' + contrato.primerApellidoCoacreditado + ' ' + contrato.segundoApellidoCoacreditado
        String nombresTitular = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido

        return "Se extiende el presente para informar que, YO " + ftr(nombres.toUpperCase(), 'n') +
                " en la fecha en que se emite este documento, acepto y reconozco el compromiso y total responsabilidad" +
                " sobre el contrato " + ftr(contratoFolio(contrato), 'n') + " firmado el d??a " +
                ftr(fechaCorta(contrato.fechaContrato), 'n') + " a nombre de " +
                ftr(nombresTitular, 'n') + ", para realizar los pagos correspondientes de " +
                "las mensualidades del pr??stamo, incluyendo los intereses y en su caso los cargos moratorios, " +
                "comisiones e I.V.A. bajo los t??rminos de dicho contrato. Todo lo anterior, aplicando las " +
                "responsabilidades y obligaciones del contrato antes mencionado, y se pueda ejercer de acuerdo con las" +
                " cl??usulas establecidas y sin deslindar de responsabilidad alguna al titular original de dicho contrato. "
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
        String consumidor = ftr("???EL CONSUMIDOR???", 'n')
        String proveedor = ftr("???EL PROVEEDOR???", 'n')
        String incisoC = "\n\n" + consumidor + " podr?? realizar pagos anticipados, siempre y cuando se encuentre al " +
                "corriente de sus pagos y haya cumplido el plazo de los 6 meses, por lo que los pagos anticipados se" +
                " aplicar??n al saldo insoluto principal. " + consumidor + " deber?? avisar por escrito 45 d??as naturales" +
                " a " + proveedor + " su deseo de realizar el 50% de aportaci??n a su capital, por lo que " + proveedor +
                " deber?? aplicar dicho pago anticipado al saldo insoluto."

        switch (duracion) {
            case 3:
                texto = "en la Car??tula del presente Contrato, conforme a las opciones de pago descritas en ??ste, " +
                        "habiendo cumplido el plazo de los tres meses y se le cobra a " + consumidor + " una penalizaci??n" +
                        " de una mensualidad del pr??stamo incluyendo los intereses y en su caso los cargos moratorios, " +
                        "Comisiones e I.V.A., con un aviso anticipado de 45 d??as naturales. Si avisa con 30 d??as naturales" +
                        " de anticipaci??n, se le aplica a " + consumidor + " una penalizaci??n de una mensualidad y media" +
                        " del pr??stamo incluyendo los intereses y en su caso los cargos moratorios, Comisiones e I.V.A.," +
                        " y si avisa con 15 d??as naturales de anticipaci??n, se le aplica a " + consumidor + " una " +
                        "penalizaci??n de dos mensualidades del pr??stamo incluyendo los intereses y en su caso los cargos" +
                        " moratorios, Comisiones e I.V.A., y en caso de no dar aviso anticipado, se le aplica a " +
                        consumidor + " la penalizaci??n de dos mensualidades y media del pr??stamo incluyendo los intereses" +
                        " y en su caso los cargos moratorios, Comisiones e I.V.A.. en cuyo caso " + consumidor + " deber??" +
                        " presentarse en el establecimiento. Efectuado el pago se proceder?? a la devoluci??n de la Prenda" +
                        " en el acto. En caso de que " + consumidor + " no cumpla con el pago en el plazo establecido de" +
                        " los 3 meses, se extender?? en autom??tico el plazo del pago anticipado a la mensualidad subsecuente" +
                        " de la 6ta mensualidad habiendo hecho el aviso anticipado de 45 d??as naturales. "
                break
            default:
                texto = "en la Car??tula del presente Contrato, conforme a las opciones de pago descritas en ??ste, " +
                        "habiendo cumplido el pazo de los seis meses , y si avisa con 45 d??as naturales de anticipaci??n," +
                        " se libera a " + consumidor + "de las penalizaciones del pr??stamo incluyendo los intereses y en " +
                        "su caso los cargos moratorios, Comisiones e I.V.A., si avisa con 30 d??as naturales de anticipaci??n," +
                        " se le aplica a " + consumidor + " una penalizaci??n de una mensualidad del pr??stamo incluyendo " +
                        "los intereses y en su caso los cargos oratorios, Comisiones e I.V.A., y si avisa con 15 d??as " +
                        "naturales de anticipaci??n, se le aplica a " + consumidor + " una penalizaci??n de una mensualidad" +
                        " y media del pr??stamo incluyendo los intereses y en su caso los cargos moratorios, Comisiones e " +
                        "I.V.A., y en caso de no dar aviso anticipado, se le aplica a " + consumidor + " la penalizaci??n" +
                        " de dos mensualidades del pr??stamo incluyendo los intereses y en su caso los cargos moratorios," +
                        " Comisiones e I.V.A. en cuyo caso " + consumidor + "deber?? presentarse en el establecimiento " +
                        "efectuado el pago se proceder?? a la devoluci??n de la Prenda en el acto.En caso de que " +
                        consumidor + " no cumpla con el pago anticipado en el plazo establecido de los 6 meses, se" +
                        " extender?? en autom??tico el plazo del pago anticipado a la mensualidad subsecuente a la fecha" +
                        " del aviso anticipado de 45 d??as naturales."

                break
        }
        return texto + incisoC
    }

    def solicitudLiquidacion(Contrato contrato) {
        String texto
        String nombres = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
        ContratoDetalle contratoDetalle = ContratoDetalle.findByContratoAndParcialidad(contrato, contrato.tipoContrato.duracion.toString())
        if (contrato.razonesSociales != null) {
            texto = "Por medio del presente documento, YO " + ftr(nombres, 'n') + " en representaci??n legal" +
                    " de " + ftr(contrato.razonesSociales.descLabel, 'n') + " en la fecha en que se emite " +
                    "este documento, solicito a " + ftr("AP SERVICIOS FINANCIEROS S.A. DE C.V.", 'n') +
                    " el t??rmino del contrato " + ftr(contratoFolio(contrato), 'n') + " celebrado el " +
                    ftr(fecha(contrato.fechaContrato), 'n') + ", y conforme a las cl??usulas que se especifican" +
                    " en el mismo, acepto y reconozco realizar el pago total de mi adeudo el d??a " +
                    ftr(fecha(contratoDetalle.fecha), 'n') + " como fecha l??mite de pago " +
                    verificarHabil(contrato) + "de acuerdo al calendario de pagos especificado en la car??tula del " +
                    "contrato antes mencionado. \nDe no ser as??, me comprometo a pagar el 10% del adeudo total por cada" +
                    " d??a de atraso.\n\n" + ftr("ES NECESARIO QUE LAS MENSUALIDADES ANTES DE LA FECHA DE " +
                    "LIQUIDACI??N DEL ADEUDO ENCUENTREN CUBIERTAS EN TIEMPO, EL IMPORTE PUEDE INCREMENTAR EN CASO DE" +
                    " INCUMPLIR CON LAS FECHAS ESTIPULADAS", 'n')
        } else {
            texto = "Por medio del presente documento, YO " + ftr(nombres, 'n') + " en la fecha en que se " +
                    "emite este documento, solicito a " + ftr("AP SERVICIOS FINANCIEROS S.A. DE C.V.", 'n') +
                    " el t??rmino del contrato " + ftr(contratoFolio(contrato), 'n') + " celebrado el " +
                    ftr(fecha(contrato.fechaContrato), 'n') + ", y conforme a las cl??usulas que se especifican" +
                    " en el mismo, acepto y reconozco realizar el pago total de mi adeudo el d??a " +
                    ftr(fecha(contratoDetalle.fecha), 'n') + " como fecha l??mite de pago " + verificarHabil(contrato) +
                    "de acuerdo al calendario de pagos especificado en la car??tula del contrato antes mencionado. \nDe" +
                    " no ser as??, me comprometo a pagar el 10% del adeudo total por cada d??a de atraso.\n\n" +
                    ftr("ES NECESARIO QUE LAS MENSUALIDADES ANTES DE LA FECHA DE LIQUIDACI??N DEL ADEUDO ENCUENTREN" +
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
        return fechaContrato == fechaLiquidacion ? '' : '(al tratarse de un dia inh??bil) '
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
}
