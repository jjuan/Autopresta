package mx.saccsa.autopresta

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import mx.saccsa.common.Parametros

import java.text.SimpleDateFormat

@Transactional
class ReporteService {
    def springSecurityService
    def sdf = new SimpleDateFormat('yyyy-MM-dd')
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
        def prestamoSobreAvaluo = ((contrato.montoRequerido / contrato.valorDeVenta) * 100)
        def lista
        if (contrato.regimenFiscal.clave == 'PM') {
            lista = [
                    listaDatos                   : listaDatos,
                    nombre                       : contrato.nombres,
                    nombreRazonSocial            : contrato.razonesSociales.razonSocial,
                    rfcRazonSocial               : contrato.razonesSociales.rfc,
                    apellidos                    : campo(contrato.primerApellido) + ' ' + campo(contrato.segundoApellido),
                    rfc                          : contrato.rfc.toUpperCase(),
                    edad                         : contrato.edad.toString(),
                    whatsapp                     : campo(contrato.telefonoCelular),
                    telTrabajoCasa               : campo(contrato.telefonoOficina) + ' ' + campo(contrato.telefonoFijo),
                    noIdentificacionOficial      : contrato.claveElector != null ? contrato.claveElector : '',
                    calleNoExterior              : campo(dir.direccionPrincipal) + ' ' + exterior,
                    noInterior                   : dir.interior != null ? dir.interior : '',
                    colonia                      : dir.colonia != null ? dir.colonia : '',
                    codigoPostal                 : dir.cp != null ? dir.cp : '',
                    alcaldia                     : dir.municipio != null ? dir.municipio : '',
                    noContrato                   : contrato.numeroContrato,
                    montoPrestamo                : contrato.montoRequerido,
                    montoTotalPagar              : montoTotal,
                    referenciaBancaria           : contrato.referencia ? contrato.referencia : '',
                    clabe                        : contrato.clabe ? contrato.clabe : '',
                    plazo                        : contrato.tipoContrato.descripcion,
                    desempeño                    : fecha(desempenio.fecha),
                    caracteristicas              : descripcion(contrato),
                    noDeVin                      : campo(contrato.numeroVin),
                    avaluo                       : contrato.valorDeVenta,
                    prestamo                     : contrato.montoRequerido,
                    prestamoSobreAvaluo          : prestamoSobreAvaluo.intValue() + '%',
                    montoPrestamoLetra           : utilService.cantidadLetra(contrato.montoRequerido, Divisas.findByClave('MXN')),
                    montoAvaluoLetra             : utilService.cantidadLetra(contrato.valorDeVenta, Divisas.findByClave('MXN')),
                    porcentajePrestamoSobreAvaluo: '(' + utilService.montoLetra(prestamoSobreAvaluo.intValue()) + ' PORCIENTO) ' + prestamoSobreAvaluo.intValue() + '%',
                    fechaLimiteFiniquito         : fecha(desempenio.fecha),
                    fecha                        : 'CDMX a ' + fecha(contrato.fechaContrato),
                    notasDescuentos              : contrato.detalleDescuentos
            ]
        } else if (contrato.nombresCoacreditado != null && contrato.regimenFiscal.clave != 'PM') {
            lista = [
                    listaDatos                         : listaDatos,
                    nombre                             : contrato.nombres,
                    apellidos                          : campo(contrato.primerApellido) + ' ' + campo(contrato.segundoApellido),
                    rfc                                : contrato.rfc.toUpperCase(),
                    edad                               : contrato.edad.toString(),
                    whatsapp                           : campo(contrato.telefonoCelular),
                    telTrabajoCasa                     : campo(contrato.telefonoOficina) + ' ' + campo(contrato.telefonoFijo),
                    noIdentificacionOficial            : contrato.claveElector != null ? contrato.claveElector : '',
                    nombreCoacreditado                 : contrato.nombresCoacreditado,
                    apellidosCoacreditado              : campo(contrato.primerApellidoCoacreditado) + ' ' + campo(contrato.segundoApellidoCoacreditado),
                    rfcCoacreditado                    : contrato.rfcCoacreditado.toUpperCase(),
                    edadCoacreditado                   : contrato.edadCoacreditado.toString(),
                    whatsappCoacreditado               : campo(contrato.telefonoCelularCoacreditado),
                    telTrabajoCasaCoacreditado         : campo(contrato.telefonoOficinaCoacreditado) + ' ' + campo(contrato.telefonoFijoCoacreditado),
                    noIdentificacionOficialCoacreditado: contrato.claveElectorCoacreditado != null ? contrato.claveElectorCoacreditado : '',
                    calleNoExterior                    : campo(dir.direccionPrincipal) + ' ' + exterior,
                    noInterior                         : dir.interior != null ? dir.interior : '',
                    colonia                            : dir.colonia != null ? dir.colonia : '',
                    codigoPostal                       : dir.cp != null ? dir.cp : '',
                    alcaldia                           : dir.municipio != null ? dir.municipio : '',
                    noContrato                         : contrato.id.toString(),
                    montoPrestamo                      : contrato.montoRequerido,
                    montoTotalPagar                    : montoTotal,
                    referenciaBancaria                 : contrato.referencia ? contrato.referencia : '',
                    clabe                              : contrato.clabe ? contrato.clabe : '',
                    plazo                              : contrato.tipoContrato.descripcion,
                    desempeño                          : fecha(desempenio.fecha),
                    caracteristicas                    : descripcion(contrato),
                    noDeVin                            : campo(contrato.numeroVin),
                    avaluo                             : contrato.valorDeVenta,
                    prestamo                           : contrato.montoRequerido,
                    prestamoSobreAvaluo                : prestamoSobreAvaluo.intValue() + '%',
                    montoPrestamoLetra                 : utilService.cantidadLetra(contrato.montoRequerido, Divisas.findByClave('MXN')),
                    montoAvaluoLetra                   : utilService.cantidadLetra(contrato.valorDeVenta, Divisas.findByClave('MXN')),
                    porcentajePrestamoSobreAvaluo      : '(' + utilService.montoLetra(prestamoSobreAvaluo.intValue()) + ' PORCIENTO) ' + prestamoSobreAvaluo.intValue() + '%',
                    fechaLimiteFiniquito               : fecha(desempenio.fecha),
                    fecha                              : 'CDMX a ' + fecha(contrato.fechaContrato),
                    notasDescuentos                    : contrato.detalleDescuentos
            ]
        } else {
            lista = [
                    listaDatos                         : listaDatos,
                    nombre                             : contrato.nombres,
                    apellidos                          : campo(contrato.primerApellido) + ' ' + campo(contrato.segundoApellido),
                    rfc                                : contrato.rfc.toUpperCase(),
                    edad                               : contrato.edad.toString(),
                    whatsapp                           : campo(contrato.telefonoCelular),
                    telTrabajoCasa                     : campo(contrato.telefonoOficina) + ' ' + campo(contrato.telefonoFijo),
                    noIdentificacionOficial            : contrato.claveElector != null ? contrato.claveElector : '',
                    calleNoExterior                    : campo(dir.direccionPrincipal) + ' ' + exterior,
                    noInterior                         : dir.interior != null ? dir.interior : '',
                    colonia                            : dir.colonia != null ? dir.colonia : '',
                    codigoPostal                       : dir.cp != null ? dir.cp : '',
                    alcaldia                           : dir.municipio != null ? dir.municipio : '',
                    noContrato                         : contrato.id.toString(),
                    montoPrestamo                      : contrato.montoRequerido,
                    montoTotalPagar                    : montoTotal,
                    referenciaBancaria                 : contrato.referencia ? contrato.referencia : '',
                    clabe                              : contrato.clabe ? contrato.clabe : '',
                    plazo                              : contrato.tipoContrato.descripcion,
                    desempeño                          : fecha(desempenio.fecha),
                    caracteristicas                    : descripcion(contrato),
                    noDeVin                            : campo(contrato.numeroVin),
                    avaluo                             : contrato.valorDeVenta,
                    prestamo                           : contrato.montoRequerido,
                    prestamoSobreAvaluo                : prestamoSobreAvaluo.intValue() + '%',
                    montoPrestamoLetra                 : utilService.cantidadLetra(contrato.montoRequerido, Divisas.findByClave('MXN')),
                    montoAvaluoLetra                   : utilService.cantidadLetra(contrato.valorDeVenta, Divisas.findByClave('MXN')),
                    porcentajePrestamoSobreAvaluo      : '(' + utilService.montoLetra(prestamoSobreAvaluo.intValue()) + ' PORCIENTO) ' + prestamoSobreAvaluo.intValue() + '%',
                    fechaLimiteFiniquito               : fecha(desempenio.fecha),
                    fecha                              : 'CDMX a ' + fecha(contrato.fechaContrato),
                    notasDescuentos                    : contrato.detalleDescuentos
                    ]
        }

        return lista
    }

    String descripcion(Contrato contrato) {
        String desc = contrato.marca.nombre.toUpperCase() + ' ' + contrato.modelo.nombre.toUpperCase() + ', COLOR ' + contrato.color.toUpperCase() + ', PLACAS ' + contrato.placas.toUpperCase()
        return desc
    }

    String campo(def s) {
        if (s == null) {
            return ''
        } else {
            return s
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
}
