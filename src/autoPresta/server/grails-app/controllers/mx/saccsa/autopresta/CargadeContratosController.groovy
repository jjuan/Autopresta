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

    def ajuste() {
        def carga = CargadeContratos.list()
        for (c in carga) {
            Contrato contrato = Contrato.findByNumeroContratoAndTipoFolio(c.numeroContrato, c.tipoContrato)
            if (contrato != null) {
                if (c.inicio == contrato.inicio && c.fin == contrato.fin) {
                    contrato.actualizado = true
                    contrato.save(flush: true, failOnError: true)
                    c.cargado = true
                    c.save(flush: true, failOnError: true)
                } else {
                    contrato.inicioReq = c.inicio
                    contrato.finReq = c.fin
                    contrato.montoReqAct = c.montoActualReq
                    contrato.actualizado = false
                    contrato.save(flush: true, failOnError: true)
                }

                if (contrato.inicioReq < contrato.inicio && contrato.actualizado == false || contrato.finReq < contrato.fin && contrato.actualizado == false) {
                    contrato.folioCarga = 500
                    contrato.save(flush: true, failOnError: true)
                }
            }
        }

        respond CargadeContratos.findAllByCargado(false).collect({
            [
                    id            : it.id,
                    numeroContrato: it.numeroContrato,
                    inicio        : Contrato.findByNumeroContratoAndTipoFolio(it.numeroContrato, it.tipoContrato) != null ? Contrato.findByNumeroContratoAndTipoFolio(it.numeroContrato, it.tipoContrato).inicio : 'No hay',
                    fin           : Contrato.findByNumeroContratoAndTipoFolio(it.numeroContrato, it.tipoContrato) != null ? Contrato.findByNumeroContratoAndTipoFolio(it.numeroContrato, it.tipoContrato).fin : 'No hay',
                    inicioReq     : it.inicio,
                    finReq        : it.fin
            ]
        })
    }

    def getGen() {
        def contratos = CargadeContratos.list()
        for (contrato in contratos) {
            if (contrato.parcialidadActual >= 37 && contrato.parcialidadActual <= 48) {
                contrato.inicio = 37
                contrato.fin = 48
            }
            if (contrato.parcialidadActual >= 25 && contrato.parcialidadActual <= 36) {
                contrato.inicio = 25
                contrato.fin = 36
            }
            if (contrato.parcialidadActual >= 13 && contrato.parcialidadActual <= 24) {
                contrato.inicio = 13
                contrato.fin = 24
            }
            if (contrato.parcialidadActual >= 1 && contrato.parcialidadActual <= 12) {
                contrato.inicio = 1
                contrato.fin = 12
            }
            contrato.save(flush: true, failOnError: true)
        }
        respond me: 'ok'
    }

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
//        Marcas marca = Marcas.findByNombre(captura.marca)
////        Marcas marcaAuto = Marcas.findByNombre(auto.marca)
//        if (marca == null) {
//            Marcas altaMarca = new Marcas()
//            altaMarca.nombre = captura.marca
//            altaMarca.slug = captura.marca
//            altaMarca.save(flush: true, failOnError: true)
//            marca = Marcas.findByNombre(captura.marca)
//        }

//        Modelos modelo = Modelos.findByMarcaAndNombre(marca, captura.modelo)

//        Modelos modelo = Modelos.findByNombre(captura.modelo)
//        if (modelo == null) {
//            Modelos altaModelo = new Modelos()
//            altaModelo.marca = marca
//            altaModelo.nombre = captura.modelo
//            altaModelo.slug = captura.modelo
//            altaModelo.save(flush: true, failOnError: true)
//            modelo = Modelos.findByMarcaAndNombre(marca, captura.modelo)
//        }
//        Modelos gps = Modelos.findByNombre(captura.gps1)
//        if (marca != null && modelo != null) {
        Contrato contrato = new Contrato()
        contrato.numeroContrato = captura.numeroContrato
        if (captura.referencia != null) {
            contrato.referencia = captura.referencia
        }
        contrato.fechaContrato = captura.fechaContrato
        if (captura.rfc != null) {
            contrato.rfc = captura.rfc
        }
        contrato.nombreLargo = captura.nombreLargo
//            contrato.marca = marca
//            contrato.modelo = modelo
        if (captura.anio) {
            contrato.anio = captura.anio
        }
        if (captura.color) {
            contrato.color = captura.color
        }
        if (captura.placas) {
            contrato.placas = captura.placas
        }
        if (captura.numeroVin) {
            contrato.numeroVin = captura.numeroVin
        }
        if (captura.montoActualReq) {
            contrato.montoRequerido = captura.montoActualReq
        }
        //        representanteLegal
//        contrato. rango
//        contrato. capturado
        if (captura.costoMensualInteres != null) {
            contrato.costoMensualInteres = captura.costoMensualInteres
        }
        if (captura.costoMensualMonitoreo != null) {
            contrato.costoMensualMonitoreo = captura.costoMensualMonitoreo
        }
        if (captura.costoMensualGPS != null) {
            contrato.costoMensualGPS = captura.costoMensualGPS
        }
        contrato.contratoMonterrey = captura.tipoContrato == 'MTY'
        if (captura.regimenFiscal != null) {
            contrato.regimenFiscal = C_RegimenFiscal.findByClave(captura.regimenFiscal)
        }
        contrato.fechaContrato = captura.fechaContrato
//            contrato.nombres = captura.nombres
//            contrato.primerApellido = captura.primerApellido
//            contrato.segundoApellido = captura.segundoApellido
        if (captura.genero != null) {
            contrato.genero = captura.genero
        }
        if (captura.rfc != null) {
            contrato.rfc = captura.rfc
        }
        if (captura.edad != "N/A") {
            contrato.edad = captura.edad as Long
        }
        if (captura.curp != null) {
            contrato.curp = captura.curp
        }
        if (captura.telefonoCelular != null) {
            contrato.telefonoCelular = captura.telefonoCelular
        }
        if (captura.telefonoOficina != null) {
            contrato.telefonoOficina = captura.telefonoOficina
        }
        if (captura.correoElectronico != null) {
            contrato.correoElectronico = captura.correoElectronico
        }
        if (captura.anio != null) {
            contrato.anio = captura.anio
        }
        if (captura.versionAuto != null) {
            contrato.versionAuto = captura.versionAuto
        }
        if (captura.color != null) {
            contrato.color = captura.color
        }
        if (captura.placas != null) {
            contrato.placas = captura.placas
        }
        if (captura.valorDeCompra != null) {
            contrato.valorDeCompra = captura.valorDeCompra
        }
        if (captura.montoMaximoAutorizado != null) {
            contrato.montoMaximoAutorizado = captura.montoMaximoAutorizado as BigDecimal
        }
        if (captura.numeroVin != null) {
            contrato.numeroVin = captura.numeroVin
        }
        contrato.gps1 = Gps.findByNombre(captura.gps1)
        if (captura.gps2 != 'Null' && captura.gps2 != null) {
            contrato.gps2 = Gps.findByNombre(captura.gps2)
        }
        if (captura.costoMensualInteres != null) {
            contrato.costoMensualInteres = captura.costoMensualInteres
        }
        if (captura.costoMensualMonitoreo != null) {
            contrato.costoMensualMonitoreo = captura.costoMensualMonitoreo
        }
        if (captura.costoMensualGPS != null) {
            contrato.costoMensualGPS = captura.costoMensualGPS
        }
        if (captura.totalAutoPresta != null) {
            contrato.totalAutoPresta = captura.totalAutoPresta
        }
        if (captura.iva != null) {
            contrato.iva = captura.iva
        }
        if (captura.costoMensualTotal != null) {
            contrato.costoMensualTotal = captura.costoMensualTotal
        }
        contrato.estatus = 'R'
        if (captura.referencia != null) {
            contrato.referencia = captura.referencia
        }
        if (captura.clabe != null) {
            contrato.clabe = captura.clabe
        }
        if (captura.numeroContrato != null) {
            contrato.numeroContrato = captura.numeroContrato
        }
        if (captura.contratoPrueba != null) {
            contrato.contratoPrueba = captura.contratoPrueba = false
        }
        if (captura.montoTransferencia != null) {
            contrato.montoTransferencia = captura.montoTransferencia as BigDecimal
        }
        if (captura.detalleDescuentos != null) {
            contrato.detalleDescuentos = captura.detalleDescuentos
        }
        if (captura.fechaSolicitud != null) {
            contrato.fechaSolicitud = captura.fechaSolicitud
        }
        if (captura.fechaCompromiso != null) {
            contrato.fechaCompromiso = captura.fechaCompromiso
        }
        if (captura.contratoMonterrey != null) {
            contrato.contratoMonterrey = captura.contratoMonterrey = false
        }
        if (captura.nombreLargo != null) {
            contrato.nombreLargo = captura.nombreLargo
        }
        if (captura.id != null) {
            contrato.folioCarga = captura.id
        }
        if (captura.montoRequerido != null) {
            contrato.montoRequerido = captura.montoRequerido
        }

        contrato.inicioReq = captura.inicio
        contrato.finReq = captura.fin
        contrato.montoReqAct = captura.montoActualReq
        contrato.actualizado = false

        contrato.save(flush: true, failOnError: true)

//        cargaDetalles(contrato, captura.inicio, captura.fin)

        captura.cargado = true
        captura.save(flush: true, failOnError: true)
    }


    def cargaDetalles(Contrato contrato, Integer inicio, Integer fin) {
        for (Integer i = inicio; i <= fin; i++) {
            def costoMensualInteres = (contrato.montoReqAct * 5) / 100
            def costoMensualMonitoreo = (contrato.montoReqAct * 1) / 100 < 800 ? 800 : (contrato.montoReqAct * 1) / 100
            def costoMensualGPS = (contrato.montoReqAct * 0.75) / 100 < 600 ? 600 : (contrato.montoReqAct * 0.75) / 100

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
            contratoDetalle.conciliado = false
            contratoDetalle.save(flush: true, failOnError: true)
            contrato.actualizado = true
            contrato.save(flush: true, failOnError: true)
        }
    }

    @Transactional
    def extender (){
        def contratos = Contrato.findAllByActualizado(false)
        for (contrato in contratos){
            cargaDetalles(contrato, contrato.inicio as Integer, contrato.fin as Integer)
        }
        respond m:'listo'
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
            cargadeContratos.fechaFin = fecha
            cargadeContratos.save(flush: true, failOnError: true)
        }

        respond message: 'ok'
    }


    def ajusteContratos() {
        def carga = CargadeContratos.findAllByCargado(false)
        for (c in carga) {
            Contrato contrato = Contrato.findByNumeroContratoAndTipoFolio(c.numeroContrato, c.tipoContrato)
            if (contrato != null) {
                log.error "folio carga: " + c.id
                log.error "folio contrato: " + contrato.id
                if (c.regimenFiscal != null) {
                    contrato.regimenFiscal = C_RegimenFiscal.findByClave(c.regimenFiscal)
                }
                if (c.genero != null) {
                    contrato.genero = c.genero
                }
                if (c.rfc != null) {
                    contrato.rfc = c.rfc
                }
                if (c.edad != null) {
                    contrato.edad = new Long(c.edad)
                }
                if (c.curp != null) {
                    contrato.curp = c.curp
                }
                if (c.telefonoCelular != null) {
                    contrato.telefonoCelular = c.telefonoCelular
                }
                if (c.correoElectronico != null) {
                    contrato.correoElectronico = c.correoElectronico
                }
                if (c.anio != null) {
                    contrato.anio = c.anio
                }
                if (c.marca != null) {

                    Marcas marca = Marcas.findByNombre(c.marca)
                    if (marca == null) {
                        Marcas altaMarca = new Marcas()
                        altaMarca.nombre = c.marca
                        altaMarca.slug = c.marca
                        altaMarca.save(flush: true, failOnError: true)
                        marca = Marcas.findByNombre(c.marca)
                        contrato.marca = marca
                    }

                    if (c.modelo != null) {
                        Modelos modelo = Modelos.findByNombre(c.modelo)
                        if (modelo == null) {
                            Modelos altaModelo = new Modelos()
                            altaModelo.marca = marca
                            altaModelo.nombre = c.modelo
                            altaModelo.slug = c.modelo
                            altaModelo.save(flush: true, failOnError: true)
                            modelo = Modelos.findByMarcaAndNombre(marca, c.modelo)
                            contrato.modelo = modelo
                        }
                    }
                    if (c.version != null) {
                        contrato.version = c.version
                    }
                    if (c.color != null) {
                        contrato.color = c.color
                    }
                    if (c.placas != null) {
                        contrato.placas = c.placas
                    }
                }
                contrato.save(flush: true, failOnError: true)
                c.cargado = true
                c.save(flush: true, failOnError: true)
            }
        }
        respond me: 'ok'
    }

}
