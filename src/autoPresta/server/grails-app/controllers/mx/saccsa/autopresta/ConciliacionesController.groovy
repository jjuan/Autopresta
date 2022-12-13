package mx.saccsa.autopresta

import mx.saccsa.common.Parametros
import mx.saccsa.restfull.CatalogoController

import grails.gorm.transactions.Transactional
import java.text.SimpleDateFormat

class ConciliacionesController extends CatalogoController<Conciliaciones> {
    ConciliacionesController() {
        super(Conciliaciones)
    }

    SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')
    def conciliacionesService
    def reporteService

    def cargarMovimientos() {
        def fechaInicio = sdf.parse(params.fechaInicio)
        def fechaFin = sdf.parse(params.fechaFin)
        String cuenta = params.cuenta ? params.cuenta as String : Parametros.getValorByParametro('CuentaAP')
        if (params.conciliados) {
            respond(conciliacionesService.cargarMovimientos(cuenta, new Boolean(params.cargoAbono), fechaInicio, fechaFin, new Boolean(params.conciliados)))
        } else {
            respond(conciliacionesService.cargarMovimientos(cuenta, new Boolean(params.cargoAbono), fechaInicio, fechaFin))
        }
    }

    def cargarParcialidades() {
        def fechaInicio = sdf.parse(params.fechaInicio)
        def fechaFin = sdf.parse(params.fechaFin)
        if (params.conciliados) {
            respond(conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, new Boolean(params.conciliados)))
        } else {
            respond(conciliacionesService.cargarParcialidades(fechaInicio, fechaFin))
        }
    }

    def cargarParcialidadesManual() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        def lista = ContratoDetalle.findAllByContratoNotInListAndFechaBetween(conciliacionesService.contratosExcluidos(), fechaInicio, fechaFin)
        lista = lista.collect({
            [folio      : it.id,
             contrato   : reporteService.contratoFolio(it.contrato),
             titular    : conciliacionesService.getTitular(it),
             parcialidad: it.parcialidad,
             fecha      : it.fecha,
             monto      : it.subtotal + it.iva,
             estatus    : ConciliacionesDetalles.findByFolioOperacion(it.id.toString()) != null ? conciliacionesService.getStatus(ConciliacionesDetalles.findByFolioOperacion(it.id.toString()), false) : 'Pendiente',
             clase      : conciliacionesService.getFolioAndClass(it).clase,
             raiz       : conciliacionesService.getRaiz(it.id.toString(), conciliacionesService.getFolioAndClass(it).clase).id,
             claseRaiz  : conciliacionesService.getRaiz(it.id.toString(), conciliacionesService.getFolioAndClass(it).clase).clase,
            ]
        })
        respond lista
    }

    def cargarParcialidadesManualNueva() {
        def fechaInicio = sdf.parse(params.fechaInicio)
        def fechaFin = sdf.parse(params.fechaFin)
//        def conciliados = Conciliaciones.findAllByConciliacionParcial(false)
        def lista = []
        def estatusContrato = ['Inpago (vendido)', 'Liquidado anticipado', 'Liquidado a tiempo', 'Fraude']
//        if (conciliados.size() > 0) {
//            def conDet = ConciliacionesDetalles.findAllByConciliacionesInList(conciliados).collect({ [folio: new Long(it.folioOperacion)] }).unique()
//            ContratoDetalle.findAllByContratoNotInListAndIdNotInListAndFechaBetween(conciliacionesService.contratosExcluidos(), conDet*.folio, fechaInicio, fechaFin)
//            lista = lista.collect({
//                [folio      : it.id,
//                 contrato   : reporteService.contratoFolio(it.contrato),
//                 titular    : conciliacionesService.getTitular(it),
//                 parcialidad: it.parcialidad,
//                 fecha      : it.fecha,
//                 monto      : it.subtotal + it.iva,
//                 estatus    : ConciliacionesDetalles.findByFolioOperacion(it.id.toString()) != null ? conciliacionesService.getStatus(ConciliacionesDetalles.findByFolioOperacion(it.id.toString()), false) : 'Pendiente',
//                 clase      : conciliacionesService.getFolioAndClass(it).clase,
//                 raiz       : conciliacionesService.getRaiz(it.id.toString(), conciliacionesService.getFolioAndClass(it).clase).id,
//                 claseRaiz  : conciliacionesService.getRaiz(it.id.toString(), conciliacionesService.getFolioAndClass(it).clase).clase,
//                ]
//            })
//        }
        lista = Contrato.findAllByFechaCorteBetweenAndEstatusContratoNotInListAndTipoFolioNotEqualAndContratoPruebaAndEstatusNotEqualAndNumeroContratoNotEqual(
                fechaInicio, fechaFin, estatusContrato, "P", false, "C", "").collect({
            ContratoDetalle cd = ContratoDetalle.findByContratoAndParcialidad(it, it.mensualidadActual as Integer)

            if (cd != null) {
                [folio      : cd.id,
                 contrato   : reporteService.contratoFolio(it),
                 titular    : conciliacionesService.getTitular2(it),
                 parcialidad: it.mensualidadActual,
                 fecha      : it.fechaCorte,
                 monto      : (cd.subtotal + cd.iva - cd.capital),
                 estatus    : ConciliacionesDetalles.findByFolioOperacion(it.id.toString()) != null ? conciliacionesService.getStatus(ConciliacionesDetalles.findByFolioOperacion(it.id.toString()), false) : 'Pendiente',
                 clase      : conciliacionesService.getFolioAndClass(it).clase,
                 raiz       : conciliacionesService.getRaiz(cd.id.toString(), conciliacionesService.getFolioAndClass(cd).clase).id,
                 claseRaiz  : conciliacionesService.getRaiz(cd.id.toString(), conciliacionesService.getFolioAndClass(cd).clase).clase,
                ]
            }
        })
        respond lista
    }

    def statusConciliacionesMovimientos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cuenta = params.cuenta ? params.cuenta as String : Parametros.getValorByParametro('CuentaAP')

        def conciliadas = conciliacionesService.cargarMovimientos(cuenta, new Boolean(params.cargoAbono), fechaInicio, fechaFin, true)
        def noConciliadas = conciliacionesService.cargarMovimientos(cuenta, new Boolean(params.cargoAbono), fechaInicio, fechaFin, false)

        respond(conciliadas: conciliadas.size(), pendientes: noConciliadas.size(), total: conciliadas.size() + noConciliadas.size())
    }

    def statusConciliacionesOperaciones() {
        def fechaInicio = sdf.parse(params.fechaInicio)
        def fechaFin = sdf.parse(params.fechaFin)

        def conciliadas = conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, true)
        def noConciliadas = conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, false)

        respond(conciliadas: conciliadas.size(), pendientes: noConciliadas.size(), total: conciliadas.size() + noConciliadas.size())
    }

    Portafolios portafolio() {
        return Portafolios.findByCvePortafolio(Parametros.getValorByParametro('Portafolio') as Integer)
    }

    def conciliacionAutomaticaMovimientos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cargoAbono = params.cargoAbono
        def concilio = conciliacionesService.conciliacionAutomaticaMovimientos(cargoAbono, fechaInicio, fechaFin, params.id as Long, true)
        respond(concilio: concilio)
        if (concilio != false) {
            respond concilio
        } else {
            respond(concilio: concilio)
        }
    }

    def conciliacionAutomaticaContratos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        def concilio = conciliacionesService.conciliacionAutomaticaContratos(fechaInicio, fechaFin, params.id as Long, true)
        respond(concilio: concilio)
    }

    def conciliacionMovimientos() {
        request.JSON
        params
        Long folioConciliacion = conciliacionesService.crearConciliacion(new BigDecimal(request.JSON.montoParcialidades as String), new BigDecimal(request.JSON.montoMovimientos as String), request.JSON.porMovimientos, request.JSON.conciliacionParcial)
        for (detalle in request.JSON.detalles) {
            conciliacionesService.crearConciliacionDetalle(folioConciliacion, getMovimiento(detalle.movimiento[0]), detalle.folioOperacion[0].toString(), detalle.tipoOperacion[0], request.JSON.formaConciliacion, getMovimiento(detalle.movimiento[0]), request.JSON.campo)
        }

        respond message: 'Conciliaciòn exitosa'
    }

    @Transactional
    def conciliacionMovimientosNueva() {
        request.JSON
        params
        ContratoDetalle cd = ContratoDetalle.findById(request.JSON.detalles[0].folioOperacion[0])
        if (cd.conciliado == true) {
            ConciliacionesDetalles conciliacionesDetalles = ConciliacionesDetalles.findByFolioOperacion(cd.id.toString())
            if (conciliacionesDetalles != null) {
                if (conciliacionesDetalles.conciliaciones.conciliacionParcial == true) {
                    def mv = request.JSON.detalles[0].movimiento[0] as String
                    LiquidacionBanco movimiento = getMovimiento(new Long(mv))
                    ConciliacionesDetalles instance = new ConciliacionesDetalles()
                    instance.conciliaciones = conciliacionesDetalles.conciliaciones
                    instance.movimiento = movimiento
                    instance.folioOperacion = cd.id.toString()
                    instance.tipoOperacion = conciliacionesService.getFolioAndClass(cd).clase
                    instance.fecha = new Date()
                    instance.usuario = conciliacionesService.getUsuario()
                    instance.formaConciliacion = request.JSON.formaConciliacion
                    instance.referenciaBancaria = movimiento.referencia
                    instance.save(flush: true, failOnError: true)
                    conciliacionesService.actualizaMovimento(movimiento.id)


                    Conciliaciones conciliacion = Conciliaciones.findById(instance.conciliaciones.id)
                    def detalles = ConciliacionesDetalles.findByConciliaciones(conciliacion)
                    def montoMov = 0
                    for (detalle in detalles) {
                        montoMov = montoMov + detalle.movimiento.monto
                    }
                    conciliacion.montoXmovimientos = montoMov
                    conciliacion.diferencia = Math.abs(conciliacion.montoXoperaciones - conciliacion.montoXmovimientos)
                    String desc = ''
                    if ((conciliacion.montoXoperaciones - conciliacion.montoXmovimientos) > 0) {
                        desc = 'Saldo en contra'
                    } else if ((conciliacion.montoXoperaciones - conciliacion.montoXmovimientos) < 0) {
                        desc = 'Saldo a favor'
                    } else if ((conciliacion.montoXoperaciones - conciliacion.montoXmovimientos) > 0) {
                        desc = 'Liquidado'
                    }
                    conciliacion.descripcionDiferencia = desc
                    conciliacion.save(flush: true, failOnError: true)

                    return instance.id
                } else {

                }
            } else {

                Long folioConciliacion = conciliacionesService.crearConciliacion(
                        new BigDecimal(request.JSON.montoParcialidades as String),
                        new BigDecimal(request.JSON.montoMovimientos as String),
                        request.JSON.porMovimientos, request.JSON.conciliacionParcial
                )
                for (detail in request.JSON.detalles) {
                    conciliacionesService.crearConciliacionDetalle(folioConciliacion, getMovimiento(detail.movimiento[0]), detail.folioOperacion[0].toString(), detail.tipoOperacion[0], request.JSON.formaConciliacion, getMovimiento(detail.movimiento[0]), request.JSON.campo)
                }
            }
        } else {

            Long folioConciliacion = conciliacionesService.crearConciliacion(
                    new BigDecimal(request.JSON.montoParcialidades as String),
                    new BigDecimal(request.JSON.montoMovimientos as String),
                    request.JSON.porMovimientos, request.JSON.conciliacionParcial
            )
            for (detail in request.JSON.detalles) {
                conciliacionesService.crearConciliacionDetalle(folioConciliacion, getMovimiento(detail.movimiento[0]), detail.folioOperacion[0].toString(), detail.tipoOperacion[0], request.JSON.formaConciliacion, getMovimiento(detail.movimiento[0]), request.JSON.campo)
            }
        }
        respond message: 'Conciliaciòn exitosa'
    }

    @Transactional
    def conciliacionMovimientosParcial() {
        request.JSON
        params
        for (detalle in request.JSON.detalles) {
            conciliacionesService.crearConciliacionDetalle(params.id as long, getMovimiento(detalle.movimiento[0]), detalle.folioOperacion[0].toString(), detalle.tipoOperacion[0], request.JSON.formaConciliacion, getMovimiento(detalle.movimiento[0]), request.JSON.campo)
        }
        Conciliaciones conciliacion = Conciliaciones.findById(params.id as long)
        conciliacion.conciliacionParcial = request.JSON.conciliacionParcial as Boolean
        conciliacion.save(flush: true)


        def detalles = ConciliacionesDetalles.findAllByConciliaciones(conciliacion)
        def montoMov = 0
        for (detalle in detalles) {
            montoMov = montoMov + detalle.movimiento.monto
        }
        conciliacion.montoXmovimientos = montoMov
        conciliacion.diferencia = Math.abs(conciliacion.montoXoperaciones - conciliacion.montoXmovimientos)
        String desc = ''
        if ((conciliacion.montoXoperaciones - conciliacion.montoXmovimientos) > 0) {
            desc = 'Saldo en contra'
        } else if ((conciliacion.montoXoperaciones - conciliacion.montoXmovimientos) < 0) {
            desc = 'Saldo a favor'
        } else if ((conciliacion.montoXoperaciones - conciliacion.montoXmovimientos) > 0) {
            desc = 'Liquidado'
        }
        conciliacion.descripcionDiferencia = desc
        conciliacion.save(flush: true, failOnError: true)

        respond message: 'Conciliaciòn exitosa'
    }

    LiquidacionBanco getMovimiento(Long id) {
        return LiquidacionBanco.findById(id)
    }


    def conciliacionGeneralContratos() {
        def conciliaciones = []
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        def parcialidades = conciliacionesService.cargarParcialidades(fechaInicio, fechaFin, false)
        for (parcialidad in parcialidades) {
            def concilio = conciliacionesService.conciliacionAutomaticaContratos(fechaInicio, fechaFin, parcialidad.folio as Long, true)
            log.debug('folio ' + parcialidad.folio + 'concilio: ' + concilio == false ? 'no' : 'si')
            if (concilio != false) {
                conciliaciones.push(concilio)
            }
        }
        def lista = []
        if (conciliaciones.size() > 0) {
            lista = ConciliacionesDetalles.findAllByConciliacionesInList(Conciliaciones.findAllByIdInList(conciliaciones)).collect({
                [concilio         : true,
                 parcialidad      : [
                         folio      : it.folioOperacion,
                         contrato   : reporteService.contratoFolio(ContratoDetalle.findById(it.folioOperacion as Long).contrato),
                         titular    : conciliacionesService.getTitular(ContratoDetalle.findById(it.folioOperacion as Long)),
                         parcialidad: ContratoDetalle.findById(it.folioOperacion as Long).parcialidad,
                         fecha      : ContratoDetalle.findById(it.folioOperacion as Long).fecha,
                         monto      : ContratoDetalle.findById(it.folioOperacion as Long).subtotal + ContratoDetalle.findById(it.folioOperacion as Long).iva,
                         estatus    : ContratoDetalle.findById(it.folioOperacion as Long).conciliado ? 'Conciliado' : 'Pendiente',
                         clase      : conciliacionesService.getFolioAndClass(ContratoDetalle.findById(it.folioOperacion as Long)).clase
                 ], movimiento    : [
                        folio     : it.movimiento.id,
                        cuenta    : it.movimiento.cuenta,
                        fecha     : it.movimiento.fecha,
                        referencia: it.movimiento.referencia,
                        monto     : it.movimiento.monto,
                        estatus   : it.movimiento.conciliado ? 'Conciliado' : 'Pendiente',
                        clase     : conciliacionesService.getFolioAndClass(it.movimiento).clase
                ],
                 formaConciliacion: it.formaConciliacion]
            })
        }
        respond lista
    }

    @Transactional
    def conciliacionGeneralMovimentos2() {
        def conciliaciones = []

        def idMovimientos = []
        def idMensualidades = []

        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        String cuenta = params.cuenta ? params.cuenta as String : Parametros.getValorByParametro('CuentaAP')
        def cuentaBancariaCliente = false
        def excluidos = Contrato.findAllByContratoPruebaOrTipoFolioOrEstatusOrNumeroContratoOrEstatusContratoOrEstatusContratoOrEstatusContratoOrEstatusContrato(true, "P", "C", "", 'Inpago (vendido)', 'Liquidado anticipado', 'Liquidado a tiempo', 'Fraude')
        def contratos = Contrato.findAllByIdNotInListAndFechaCorteLessThanEqualsAndMensualidadActualIsNotNull(excluidos*.id, fechaFin).sort({ it.numeroContrato.length() }).reverse()

        def parcialidades = []
        for (contrato in contratos) {
            parcialidades.push(ContratoDetalle.findByParcialidadAndContrato(contrato.mensualidadActual as Integer, contrato))
        }
        def contador = 0
        for (parcialidad in parcialidades) {
            def movimientos = LiquidacionBanco.findAllByConciliadoAndCargoAbonoAndFechaBetween(false, false, fechaInicio, fechaFin)
            if (movimientos.size() > 0) {
                contador = contador + 1
//                log.error "MV: " + movimientos.size().toString() + " Parcialidad " + contador + ' de ' + parcialidades.size().toString()
                def concilio = false
                LiquidacionBanco mv = null
                String formaConciliacion = ""
                String campo = ""
                HojaConciliacion hojaConciliacion = conciliacionesService.verificarHoja(parcialidad.contrato)
                String contrato = parcialidad.contrato.numeroContrato
//            contrato
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('pago ' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('pago 0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('pago' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('pago0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%contrato ' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%contrato 0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%contrato' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%contrato0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%contrat0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%extension' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%extension ' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%extension 0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%ext' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%extension0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%concepto' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%concepto ' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%concepto 0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv == null && contrato.length() > 1) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%concepto0' + contrato + '%', false, false, fechaInicio, fechaFin)
                }
                if (mv != null) {
                    hojaConciliacion.regla3 = contrato
                    campo = contrato
                    formaConciliacion = 'Número de contrato'
                }


                String cliente = parcialidad.contrato.razonesSociales != null ? parcialidad.contrato.razonesSociales.razonSocial.toLowerCase() : conciliacionesService.nombreCliente(parcialidad.contrato)
                String referencia = parcialidad.contrato.referencia != null ? parcialidad.contrato.referencia.toLowerCase() : null
                String rfc = parcialidad.contrato.razonesSociales != null ? parcialidad.contrato.razonesSociales.rfc.toLowerCase() : parcialidad.contrato.rfc != null ? parcialidad.contrato.rfc.toLowerCase() : null
                String placas = parcialidad.contrato.placas != null ? parcialidad.contrato.placas.toLowerCase() : null

                if (mv == null && cliente != null) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%' + cliente + '%', false, false, fechaInicio, fechaFin)
                    if (mv != null) {
                        hojaConciliacion.regla1 = cliente
                        campo = cliente
                        formaConciliacion = 'Nombre del cliente'
                    }
                }


                if (mv == null && referencia != null) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('% ' + referencia + '%', false, false, fechaInicio, fechaFin)
                    if (mv != null) {
                        hojaConciliacion.regla2 = referencia
                        campo = referencia
                        formaConciliacion = 'Referencia bancaria'
                    }
                }

                if (mv == null && rfc != null) {
                    mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%' + rfc + '%', false, false, fechaInicio, fechaFin)
                    if (mv != null) {
                        hojaConciliacion.regla5 = rfc
                        campo = rfc
                        formaConciliacion = 'RFC del cliente'
                    }
                }

                if (mv == null && placas != null) {
                    if (placas.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetween('%' + placas + '%', false, false, fechaInicio, fechaFin)
                        if (mv != null) {
                            hojaConciliacion.regla6 = placas
                            campo = placas
                            formaConciliacion = 'Placas del vehículo del cliente'
                        }
                    }
                }

                if (mv != null) {
                    def operacion = conciliacionesService.getFolioAndClass(parcialidad)
                    def conciliacion = conciliacionesService.crearConciliacion(parcialidad.iva + parcialidad.subtotal, mv.monto, false, true)
                    conciliacionesService.crearConciliacionDetalle(conciliacion, mv, operacion.folio, operacion.clase, formaConciliacion, parcialidad, campo)
                    hojaConciliacion.save(flush: true, failOnError: true)
//                    conciliaciones.push(conciliacion)

                    concilio = [concilio: true, parcialidad: [
                            folio      : parcialidad.id,
                            contrato   : reporteService.contratoFolio(parcialidad.contrato),
                            titular    : conciliacionesService.getTitular(parcialidad),
                            parcialidad: parcialidad.parcialidad,
                            fecha      : parcialidad.fecha,
                            monto      : parcialidad.subtotal + parcialidad.iva,
                            estatus    : parcialidad.conciliado ? 'Conciliado' : 'Pendiente',
                            clase      : conciliacionesService.getFolioAndClass(parcialidad).clase
                    ], movimiento       : [
                            folio     : mv.id,
                            cuenta    : mv.cuenta,
                            fecha     : mv.fecha,
                            referencia: mv.referencia,
                            monto     : mv.monto,
                            estatus   : mv.conciliado ? 'Conciliado' : 'Pendiente',
                            clase     : conciliacionesService.getFolioAndClass(mv).clase
                    ], formaConciliacion: formaConciliacion]
                }

                if (concilio != false) {
                    conciliaciones.push(concilio)
                    idMovimientos.push(concilio.movimiento.folio)
                    idMensualidades.push(concilio.parcialidad.folio)
                }
            } else {
                respond conciliaciones
                return
            }
        }
//
//        def movimientos = LiquidacionBanco.findAllByCuentaAndCargoAbonoAndConciliadoAndFechaBetweenAndIdNotInList(cuenta, false, false, fechaInicio, fechaFin, idMovimientos)
//        if (movimientos.size() > 0) {
//            for (movimiento in movimientos) {
//
//                def concilio = conciliacionesService.conciliacionAutomaticaMovimientos("false", fechaInicio, fechaFin, movimiento.id, idMensualidades, true)
//                if (concilio != false) {
//                    conciliaciones.push(concilio)
//                }
//            }
//        }


//        parcialidades = ContratoDetalle.findAllByContratoInListAndConciliadoAndFechaLessThanEquals(contratos*.id, false, fechaFin)
        def LB = LiquidacionBanco.findAllByConciliadoAndCargoAbonoAndFechaBetween(false, false, fechaInicio, fechaFin)
//        log.error "MV: " + LB.size().toString()
        if (LB.size() > 0) {
            contador = 0
            for (parcialidad in parcialidades) {
                def movimientos = LiquidacionBanco.findAllByConciliadoAndCargoAbonoAndFechaBetween(false, false, fechaInicio, fechaFin)
                if (movimientos.size() > 0) {
                    contador = contador + 1
//                    log.error "MV: " + movimientos.size().toString() + " Parcialidad " + contador + ' de ' + parcialidades.size().toString()
                    def concilio = false
                    LiquidacionBanco mv = null
                    String formaConciliacion = ""
                    String campo = ""
                    HojaConciliacion hojaConciliacion = conciliacionesService.verificarHoja(parcialidad.contrato)
                    String contrato = parcialidad.contrato.numeroContrato
//            contrato
                    if (mv == null && contrato.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetweenAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLike(
                                '% ' + contrato + ' %', false, false, fechaInicio, fechaFin,
                                '% BNET    ' + contrato + '%',
                                '%SPEI RECIBIDOINBURSA/' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/' + contrato + '%',
                                contrato + '/%',
                                '%SPEI RECIBIDOSTP/' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ ' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ ' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ ' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ ' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ ' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ ' + contrato + '%',
                                contrato + ' /%',
                                '%SPEI RECIBIDOSTP/ ' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** ' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ ' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ ' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/0' + contrato + '%',
                                contrato + '0/%',
                                '%SPEI RECIBIDOSTP/0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/0' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ 0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ 0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ 0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ 0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ 0' + contrato + '%',
                                contrato + '0 /%',
                                '%SPEI RECIBIDOSTP/ 0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** 0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ 0' + contrato + '%'
                        )
                    }


                    if (mv == null && contrato.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetweenAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLike(
                                '% ' + contrato, false, false, fechaInicio, fechaFin, '% BNET    ' + contrato + '%',
                                '%SPEI RECIBIDOINBURSA/' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/' + contrato + '%',
                                contrato + '/%',
                                '%SPEI RECIBIDOSTP/' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ ' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ ' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ ' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ ' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ ' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ ' + contrato + '%',
                                contrato + ' /%',
                                '%SPEI RECIBIDOSTP/ ' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** ' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ ' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ ' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/0' + contrato + '%',
                                contrato + '0/%',
                                '%SPEI RECIBIDOSTP/0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/0' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ 0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ 0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ 0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ 0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ 0' + contrato + '%',
                                contrato + '0 /%',
                                '%SPEI RECIBIDOSTP/ 0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** 0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ 0' + contrato + '%')
                    }
                    if (mv == null && contrato.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetweenAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLike('% ' + contrato + '%', false, false, fechaInicio, fechaFin, '% BNET    ' + contrato + '%',
                                '%SPEI RECIBIDOINBURSA/' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/' + contrato + '%',
                                contrato + '/%',
                                '%SPEI RECIBIDOSTP/' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ ' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ ' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ ' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ ' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ ' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ ' + contrato + '%',
                                contrato + ' /%',
                                '%SPEI RECIBIDOSTP/ ' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** ' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ ' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ ' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/0' + contrato + '%',
                                contrato + '0/%',
                                '%SPEI RECIBIDOSTP/0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/0' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ 0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ 0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ 0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ 0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ 0' + contrato + '%',
                                contrato + '0 /%',
                                '%SPEI RECIBIDOSTP/ 0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** 0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ 0' + contrato + '%')
                    }
                    if (mv == null && contrato.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetweenAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLike('%' + contrato + ' %', false, false, fechaInicio, fechaFin, '% BNET    ' + contrato + '%',
                                '%SPEI RECIBIDOINBURSA/' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/' + contrato + '%',
                                contrato + '/%',
                                '%SPEI RECIBIDOSTP/' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ ' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ ' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ ' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ ' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ ' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ ' + contrato + '%',
                                contrato + ' /%',
                                '%SPEI RECIBIDOSTP/ ' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** ' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ ' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ ' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/0' + contrato + '%',
                                contrato + '0/%',
                                '%SPEI RECIBIDOSTP/0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/0' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ 0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ 0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ 0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ 0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ 0' + contrato + '%',
                                contrato + '0 /%',
                                '%SPEI RECIBIDOSTP/ 0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** 0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ 0' + contrato + '%')
                    }
                    if (mv == null && contrato.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetweenAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLike('% 0' + contrato + ' %', false, false, fechaInicio, fechaFin, '% BNET    ' + contrato + '%',
                                '%SPEI RECIBIDOINBURSA/' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/' + contrato + '%',
                                contrato + '/%',
                                '%SPEI RECIBIDOSTP/' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ ' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ ' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ ' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ ' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ ' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ ' + contrato + '%',
                                contrato + ' /%',
                                '%SPEI RECIBIDOSTP/ ' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** ' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ ' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ ' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/0' + contrato + '%',
                                contrato + '0/%',
                                '%SPEI RECIBIDOSTP/0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/0' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ 0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ 0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ 0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ 0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ 0' + contrato + '%',
                                contrato + '0 /%',
                                '%SPEI RECIBIDOSTP/ 0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** 0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ 0' + contrato + '%')
                    }
                    if (mv == null && contrato.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetweenAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLike('% 0' + contrato + '%', false, false, fechaInicio, fechaFin, '% BNET    ' + contrato + '%',
                                '%SPEI RECIBIDOINBURSA/' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/' + contrato + '%',
                                contrato + '/%',
                                '%SPEI RECIBIDOSTP/' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ ' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ ' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ ' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ ' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ ' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ ' + contrato + '%',
                                contrato + ' /%',
                                '%SPEI RECIBIDOSTP/ ' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** ' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ ' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ ' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/0' + contrato + '%',
                                contrato + '0/%',
                                '%SPEI RECIBIDOSTP/0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/0' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ 0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ 0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ 0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ 0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ 0' + contrato + '%',
                                contrato + '0 /%',
                                '%SPEI RECIBIDOSTP/ 0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** 0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ 0' + contrato + '%')
                    }
                    if (mv == null && contrato.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetweenAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLike('%0' + contrato + ' %', false, false, fechaInicio, fechaFin, '% BNET    ' + contrato + '%',
                                '%SPEI RECIBIDOINBURSA/' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/' + contrato + '%',
                                contrato + '/%',
                                '%SPEI RECIBIDOSTP/' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ ' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ ' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ ' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ ' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ ' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ ' + contrato + '%',
                                contrato + ' /%',
                                '%SPEI RECIBIDOSTP/ ' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** ' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ ' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ ' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/0' + contrato + '%',
                                contrato + '0/%',
                                '%SPEI RECIBIDOSTP/0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/0' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ 0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ 0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ 0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ 0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ 0' + contrato + '%',
                                contrato + '0 /%',
                                '%SPEI RECIBIDOSTP/ 0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** 0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ 0' + contrato + '%')
                    }
                    if (mv == null && contrato.length() > 1) {
                        mv = LiquidacionBanco.findByReferenciaLikeAndConciliadoAndCargoAbonoAndFechaBetweenAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLikeAndReferenciaNotLike('%0' + contrato + '%', false, false, fechaInicio, fechaFin, '% BNET    ' + contrato + '%',
                                '%SPEI RECIBIDOINBURSA/' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/' + contrato + '%',
                                contrato + '/%',
                                '%SPEI RECIBIDOSTP/' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ ' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ ' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ ' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ ' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ ' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ ' + contrato + '%',
                                contrato + ' /%',
                                '%SPEI RECIBIDOSTP/ ' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** ' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ ' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ ' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/0' + contrato + '%',
                                contrato + '0/%',
                                '%SPEI RECIBIDOSTP/0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/******0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/0' + contrato + '%',

                                '%SPEI RECIBIDOINBURSA/ 0' + contrato + '%',
                                '%SPEI RECIBIDOAZTECA/ 0' + contrato + '%',
                                '%PAGO CUENTA DE TERCERO/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSANTANDER/ 0' + contrato + '%',
                                '%SPEI RECIBIDOBANAMEX/ 0' + contrato + '%',
                                '%SPEI RECIBIDOHSBC/ 0' + contrato + '%',
                                contrato + '0 /%',
                                '%SPEI RECIBIDOSTP/ 0' + contrato + '%',
                                '%DEPOSITO EFECTIVO PRACTIC/****** 0' + contrato + '%',
                                '%SPEI RECIBIDOBANORTE/ 0' + contrato + '%',
                                '%SPEI RECIBIDOSCOTIABANK/ 0' + contrato + '%')
                    }
                    if (mv != null) {

                        String reference = mv.referencia

                        def corte = getPrimerCadena(reference)

                        hojaConciliacion.regla3 = contrato
                        campo = contrato
                        formaConciliacion = 'Número de contrato'
                        cuentaBancariaCliente = corte
                        if (corte != null && corte.contains(contrato) == true) {
                            mv = null
                            log.error('Lo tiene')
                        }
                    }

                    if (mv != null) {
                        def operacion = conciliacionesService.getFolioAndClass(parcialidad)
                        def conciliacion = conciliacionesService.crearConciliacion(parcialidad.iva + parcialidad.subtotal, mv.monto, false, true)
                        if (cuentaBancariaCliente != false) {
                            conciliacionesService.crearConciliacionDetalle(conciliacion, mv, operacion.folio, operacion.clase, formaConciliacion, parcialidad, campo, cuentaBancariaCliente)
                        } else {
                            conciliacionesService.crearConciliacionDetalle(conciliacion, mv, operacion.folio, operacion.clase, formaConciliacion, parcialidad, campo)
                        }
                        hojaConciliacion.save(flush: true, failOnError: true)
//                    conciliaciones.push(conciliacion)

                        concilio = [concilio: true, parcialidad: [
                                folio      : parcialidad.id,
                                contrato   : reporteService.contratoFolio(parcialidad.contrato),
                                titular    : conciliacionesService.getTitular(parcialidad),
                                parcialidad: parcialidad.parcialidad,
                                fecha      : parcialidad.fecha,
                                monto      : parcialidad.subtotal + parcialidad.iva,
                                estatus    : parcialidad.conciliado ? 'Conciliado' : 'Pendiente',
                                clase      : conciliacionesService.getFolioAndClass(parcialidad).clase
                        ], movimiento       : [
                                folio     : mv.id,
                                cuenta    : mv.cuenta,
                                fecha     : mv.fecha,
                                referencia: mv.referencia,
                                monto     : mv.monto,
                                estatus   : mv.conciliado ? 'Conciliado' : 'Pendiente',
                                clase     : conciliacionesService.getFolioAndClass(mv).clase
                        ], formaConciliacion: formaConciliacion]
                    }

                    if (concilio != false) {
                        conciliaciones.push(concilio)
                        idMovimientos.push(concilio.movimiento.folio)
                        idMensualidades.push(concilio.parcialidad.folio)
                        log.error "mov: " + concilio.movimiento.folio + " parcialidad: " + concilio.parcialidad.folio + 'Campo ' + campo
                    }
                } else {
                    respond conciliaciones
                    return
                }
            }
        }
        respond conciliaciones
    }

    @Transactional
    def conciliacionGeneralMovimentos() {
        def fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        def fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null

        def conciliaciones = []
        def idMovimientos = []
        def idMensualidades = []

        def liquidaciones = LiquidacionBanco.findAllByConciliadoAndCargoAbonoAndFechaBetween(false, false, fechaInicio, fechaFin)

        if (liquidaciones.size() == 0) {
            respond conciliaciones
        }

        for (liquidacion in liquidaciones) {
            def concilio = conciiliaReferencia(liquidacion, fechaFin)
            if (concilio != false) {
                conciliaciones.push(concilio)
                idMovimientos.push(concilio.movimiento.folio)
                idMensualidades.push(concilio.parcialidad.folio)
            }
        }

        respond conciliaciones
    }

    def conciiliaReferencia(LiquidacionBanco lb, def fechaFin) {
        def refPrincipal = lb.afavor
        String cadena = ""
        String numRef = ""
        String alfaRef = ""
        String tipo = ""
        String campo = ""
        String formaConciliacion = ""
        def concilio = false
        if (refPrincipal.startsWith('CE')) {
            cadena = (refPrincipal.replaceFirst('CE', '')).trim()
            numRef = (cadena.substring(0, cadena.indexOf('/'))).replaceFirst("^0*", "")
            if (numRef.length() == 5) {
                numRef = (numRef.substring(0, numRef.length() - 1))
            }
            alfaRef = (cadena.substring((cadena.indexOf('/')) + 8, cadena.length() - 7)).trim();
            tipo = "CE"
        }
        if (refPrincipal.startsWith('SPEI RECIBIDO')) {
            def str = refPrincipal.split(' ')
            def remover = str[0] + ' ' + str[1] + ' ' + str[2] + ' ' + str[3] + ' '
            cadena = (refPrincipal.replace(remover, '')).trim()
            numRef = (cadena.substring(0, 7)).replaceFirst("^0*", "");
            alfaRef = cadena.substring(7, cadena.length());
            tipo = 'SPEI'
        }
        if (refPrincipal.startsWith('PAGO CUENTA DE TERCERO/')) {
            def str = refPrincipal.split(' ')
            def remover = str[0] + ' ' + str[1] + ' ' + str[2] + ' ' + str[3] + ' ' + str[4] + ' ' + str[5] + ' ' + str[6]
            cadena = (refPrincipal.replace(remover, '')).trim()
            alfaRef = (cadena.substring(10, cadena.length())).trim()
            tipo = "PAGO CUENTA DE TERCERO"
        }
        if (refPrincipal.startsWith('DEPOSITO EFECTIVO PRACTIC/')) {
            def remover = "DEPOSITO EFECTIVO PRACTIC/******4176 "
            cadena = (refPrincipal.replace(remover, '')).trim()
            alfaRef = (cadena.substring(0, cadena.length() - 15)).trim();
            tipo = "DEPOSITO EFECTIVO PRACTIC"
        }
        if (refPrincipal.startsWith('DEPOSITO EN EFECTIVO')) {
            def remover = "DEPOSITO EN EFECTIVO/"
            alfaRef = (refPrincipal.replace(remover, '')).trim()
//            alfaRef = (cadena.substring(cadena.length() - 4, cadena.length())).trim();
            tipo = "DEPOSITO EN EFECTIVO"
        }
        if (alfaRef == "" && numRef == "") {
            log.error refPrincipal
        }
        alfaRef = alfaRef.replace(' 0', ' ')
        def tipoContrato = 'CDMX'
        if (alfaRef.contains('GDL')) {
            tipoContrato = 'GDL'
        } else if (alfaRef.contains('MTY')) {
            tipoContrato = 'MTY'
        }

        char[] chars = alfaRef.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        String numeroContrato = ""
        if (sb.length() > 0) {
            numeroContrato = sb.replaceFirst("^0*", "");
        }
        if (numeroContrato.length() > 4){
            numeroContrato = numeroContrato.substring(numeroContrato.length() - 4, numeroContrato.length())
        }
//        if (numeroContrato != "" || numRef.length() > 1 && numRef.length() <= 4) {
//            log.error tipo + " Num. Ref.: " + numRef + " Alfa. Ref.: " + alfaRef + tipoContrato + ' contrato: ' + numeroContrato
//        }

        Contrato contrato = null
        ContratoDetalle parcialidad = null
        if (numeroContrato != "") {
            log.error "Dato " + numeroContrato
            contrato = Contrato.findByNumeroContratoAndTipoFolio(numeroContrato, tipoContrato)
            if(contrato != null) {
                parcialidad = ContratoDetalle.findByContratoAndParcialidad(contrato, contrato.mensualidadActual as Integer)
                formaConciliacion = "Numero de Contrato"
                campo = numeroContrato
            }
        } else if (contrato == null && numRef.length() > 1 && numRef.length() <= 4) {
            log.error "Dato " + numRef
            contrato = Contrato.findByNumeroContratoAndTipoFolio(numRef, 'CDMX')
            if(contrato != null) {
                parcialidad = ContratoDetalle.findByContratoAndParcialidad(contrato, contrato.mensualidadActual as Integer)
                formaConciliacion = "Numero de Contrato"
                campo = numRef
            }
        } else {
            def excluidos = Contrato.findAllByContratoPruebaOrTipoFolioOrEstatusOrNumeroContratoOrEstatusContratoOrEstatusContratoOrEstatusContratoOrEstatusContrato(true, "P", "C", "", 'Inpago (vendido)', 'Liquidado anticipado', 'Liquidado a tiempo', 'Fraude')
            def contratos = Contrato.findAllByIdNotInListAndFechaCorteLessThanEquals(excluidos*.id, fechaFin)
            def hojas = HojaConciliacion.findByFolioInList(contratos)

            for (hoja in hojas){
//                Regla 1
                if (hoja.regla1 != null && hoja.regla1 != "" && campo != ""){
                    if (alfaRef.contains(hoja.regla1)){
                        formaConciliacion = "Nombre del cliente"
                        campo = hoja.regla1
                    }
                }
//                Regla 2
                if (hoja.regla2 != null && hoja.regla2 != "" && campo != ""){
                    if (alfaRef.contains(hoja.regla2)){
                        formaConciliacion = "Referencia bancaria"
                        campo = hoja.regla2
                    }
                }
//                Regla 5
                if (hoja.regla5 != null && hoja.regla5 != "" && campo != ""){
                    if (alfaRef.contains(hoja.regla5)){
                        formaConciliacion = "RFC del cliente"
                        campo = hoja.regla5
                    }
                }
//                Regla 6
                if (hoja.regla6 != null && hoja.regla6 != "" && campo != ""){
                    if (alfaRef.contains(hoja.regla6)){
                        formaConciliacion = "Placas del vehículo del cliente"
                        campo = hoja.regla6
                    }
                }

                if (campo != ""){
                    parcialidad = ContratoDetalle.findByContratoAndParcialidad(hoja.folio, hoja.folio.mensualidadActual as Integer)
                }
            }
        }

        if (parcialidad != null) {
            def operacion = conciliacionesService.getFolioAndClass(parcialidad)
            def conciliacion = conciliacionesService.crearConciliacion(parcialidad.iva + parcialidad.subtotal, lb.monto, false, true)
            conciliacionesService.crearConciliacionDetalle(conciliacion, lb, operacion.folio, operacion.clase, formaConciliacion, parcialidad, campo)

            concilio = [concilio: true, parcialidad: [
                    folio      : parcialidad.id,
                    contrato   : reporteService.contratoFolio(parcialidad.contrato),
                    titular    : conciliacionesService.getTitular(parcialidad),
                    parcialidad: parcialidad.parcialidad,
                    fecha      : parcialidad.fecha,
                    monto      : parcialidad.subtotal + parcialidad.iva,
                    estatus    : parcialidad.conciliado ? 'Conciliado' : 'Pendiente',
                    clase      : conciliacionesService.getFolioAndClass(parcialidad).clase
            ], movimiento       : [
                    folio     : lb.id,
                    cuenta    : lb.cuenta,
                    fecha     : lb.fecha,
                    referencia: lb.referencia,
                    monto     : lb.monto,
                    estatus   : lb.conciliado ? 'Conciliado' : 'Pendiente',
                    clase     : conciliacionesService.getFolioAndClass(lb).clase
            ], formaConciliacion: formaConciliacion]
        }
        return concilio
    }

    def verConciliacion() {
        String folio = params.folio
        String clase = params.clase

        ConciliacionesDetalles conciliacionesDetalles = clase == 'LiquidacionBanco' ? ConciliacionesDetalles.findByMovimiento(LiquidacionBanco.findById(folio as Long)) : ConciliacionesDetalles.findByTipoOperacionAndFolioOperacion(clase, folio)

        def conciliacion = Conciliaciones.findById(conciliacionesDetalles.conciliaciones.id).collect({
            [
                    id                   : it.id,
                    fechaConciliacion    : it.fechaConciliacion,
                    montoXoperaciones    : it.montoXoperaciones,
                    montoXmovimientos    : it.montoXmovimientos,
                    diferencia           : it.diferencia,
                    descripcionDiferencia: it.descripcionDiferencia,
                    conciliacionParcial  : it.conciliacionParcial,
                    detalles             : getDetalles(it),
                    porMovimiento        : it.porMovimiento
            ]
        })
        respond conciliacion
    }

    def getDetalles(Conciliaciones conciliaciones) {
        return ConciliacionesDetalles.findAllByConciliaciones(conciliaciones).collect({
            [
                    id               : it.id,
                    movimiento       : LiquidacionBanco.findById(it.movimiento.id).collect({
                        [folio     : it.id,
                         cuenta    : it.cuenta,
                         fecha     : it.fecha,
                         referencia: it.referencia,
                         monto     : it.monto,
                         estatus   : it.conciliado ? 'Conciliado' : 'Pendiente',
                         clase     : conciliacionesService.getFolioAndClass(it).clase]
                    }),
                    operacion        : ContratoDetalle.findById(it.folioOperacion as Long).collect({
                        [folio      : it.id,
                         contrato   : reporteService.contratoFolio(it.contrato),
                         titular    : conciliacionesService.getTitular(it),
                         parcialidad: it.parcialidad,
                         fecha      : it.fecha,
                         monto      : it.subtotal + it.iva,
                         estatus    : it.conciliado ? 'Conciliado' : 'Pendiente',
                         clase      : conciliacionesService.getFolioAndClass(it).clase]
                    }),
                    fecha            : it.fecha,
                    usuario          : it.usuario.descLabel,
                    formaConciliacion: it.formaConciliacion
            ]
        })
    }

    @Transactional
    def eliminarConciliacion() {
        Conciliaciones conciliacion = Conciliaciones.findById(params.id as Long)
        def detalles = ConciliacionesDetalles.findAllByConciliaciones(conciliacion)
        for (detalle in detalles) {
            LiquidacionBanco liquidacionBanco = LiquidacionBanco.findById(detalle.movimiento.id)
            liquidacionBanco.conciliado = false
            liquidacionBanco.save(flush: true)
            ContratoDetalle contratoDetalle = ContratoDetalle.findById(detalle.folioOperacion as long)
            contratoDetalle.conciliado = false
            contratoDetalle.save(flush: true)
            detalle.delete(flush: true)
        }
        conciliacion.delete(flush: true)
        respond message: 'ok'
    }

    @Transactional
    def eliminarConciliacionDetalle() {
        ConciliacionesDetalles detalle = ConciliacionesDetalles.findById(params.id as Long)
        Conciliaciones conciliacion = Conciliaciones.findById(detalle.conciliaciones.id)
        if (detalle.conciliaciones.porMovimiento) {
            ContratoDetalle contratoDetalle = ContratoDetalle.findById(detalle.folioOperacion as long)
            contratoDetalle.conciliado = false
            contratoDetalle.save(flush: true)
            conciliacion.montoXoperaciones = conciliacion.montoXoperaciones - (contratoDetalle.subtotal + contratoDetalle.iva)
        } else {
            LiquidacionBanco liquidacionBanco = LiquidacionBanco.findById(detalle.movimiento.id)
            liquidacionBanco.conciliado = false
            liquidacionBanco.save(flush: true)
            conciliacion.montoXmovimientos = conciliacion.montoXmovimientos - liquidacionBanco.monto
        }
        conciliacion.diferencia = conciliacionesService.calculoDiferencia(conciliacion.montoXoperaciones, conciliacion.montoXmovimientos).diferencia
        conciliacion.descripcionDiferencia = conciliacionesService.calculoDiferencia(conciliacion.montoXoperaciones, conciliacion.montoXmovimientos).descripcionDiferencia
        conciliacion.save(flush: true)
        detalle.delete(flush: true)


        def detalles = ConciliacionesDetalles.findAllByConciliaciones(conciliacion)
        if (detalles.size() == 0) {
            conciliacion.delete(flush: true)
        }
//        }
        respond message: 'ok'
    }

    def getStatus(String nc) {
        respond Contrato.findByNumeroContrato(nc).collect({
            [
                    numeroContrato: it.numeroContrato,
                    titular       : getTitular(it),
                    referencia    : it.referencia != null ? it.referencia : 'No tiene',
                    rfc           : it.rfc != null ? it.rfc : 'No tiene',
                    placas        : it.placas != null ? it.placas : 'No tiene',
                    modelo        : it.modelo != null ? it.modelo.descLabel : 'No tiene',
                    mensualidades : getMensualidades(it)
            ]
        })
    }

    def getMensualidades(Contrato contrato) {
        return ContratoDetalle.findAllByContrato(contrato).collect({
            [
                    id               : it.id,
                    parcialidad      : it.parcialidad,
                    fecha            : it.fecha,
                    total            : it.subtotal + it.iva,
                    conciliado       : it.conciliado,
                    folioConciliacion: it.conciliado == true ? ConciliacionesDetalles.findByFolioOperacion(it.id.toString()) != null ? ConciliacionesDetalles.findByFolioOperacion(it.id.toString()).conciliaciones.id : 'Esta conciliado por estatus' : 'No tiene'
            ]
        })
    }

    def getTitular(Contrato contrato) {
        String titular = ''
        if (contrato.razonesSociales != null) {
            titular = contrato.razonesSociales.razonSocial
        } else {
            if (contrato.nombreLargo != null) {
                titular = contrato.nombreLargo
            } else {

                titular = contrato.nombres + ' ' + contrato.primerApellido + ' ' + contrato.segundoApellido
            }
        }
        return titular
    }

    def getPrimerCadena(String ref) {
        def cadenas
        String cadena
        if (ref.contains('SPEI RECIBIDOINBURSA/')) {
            cadena = ref.replace('SPEI RECIBIDOINBURSA/', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('SPEI RECIBIDOAZTECA/')) {
            cadena = ref.replace('SPEI RECIBIDOAZTECA/', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('PAGO CUENTA DE TERCERO/ ')) {
            cadena = ref.replace('PAGO CUENTA DE TERCERO/ ', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('SPEI RECIBIDOSANTANDER/')) {
            cadena = ref.replace('SPEI RECIBIDOSANTANDER/', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('SPEI RECIBIDOBANAMEX/')) {
            cadena = ref.replace('SPEI RECIBIDOBANAMEX/', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('DEPOSITO EFECTIVO PRACTIC/******')) {
            cadena = ref.replace('DEPOSITO EFECTIVO PRACTIC/******', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('SPEI RECIBIDOHSBC/')) {
            cadena = ref.replace('SPEI RECIBIDOHSBC/', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('SPEI RECIBIDOSTP/')) {
            cadena = ref.replace('SPEI RECIBIDOSTP/', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('SPEI RECIBIDOBANORTE/')) {
            cadena = ref.replace('SPEI RECIBIDOBANORTE/', '')
            cadenas = cadena.split(" ")
        }
        if (ref.contains('SPEI RECIBIDOSCOTIABANK/')) {
            cadena = ref.replace('SPEI RECIBIDOSCOTIABANK/', '')
            cadenas = cadena.split(" ")
        }

        return cadenas != null ? cadenas[0] : null
    }

    @Transactional
    def cerrarConciliacion() {
        request.JSON.id
        ContratoDetalle cd = ContratoDetalle.findById(request.JSON.id as long)
        ConciliacionesDetalles conciliacionesDetalles = ConciliacionesDetalles.findByFolioOperacion(cd.id.toString())
        Conciliaciones conciliaciones = Conciliaciones.findById(conciliacionesDetalles.conciliaciones.id)
        conciliaciones.conciliacionParcial = !conciliaciones.conciliacionParcial
        conciliaciones.save(flush: true, failOnError: true)
        respond message: 'OK'
    }

    def getMontoConciliado() {
        respond(ContratoDetalle.findById(params.id as Long).collect({
            [
                    id                : it.id,
                    subtotal          : it.subtotal ? it.subtotal : 0.00,
                    iva               : it.iva ? it.iva : 0.00,
                    moratorios        : it.moratorios ? it.moratorios : 0.00,
                    penalizacion      : it.penalizacion ? it.penalizacion : 0.00,
                    ventaDeAutos      : it.ventaDeAutos ? it.ventaDeAutos : 0.00,
                    aportacionAcapital: it.aportacionAcapital ? it.aportacionAcapital : 0.00,
                    montoConciliado   : it.montoConciliado ? it.montoConciliado : 0.00
            ]
        }))
    }

    @Transactional
    def guardarMonto(Long id) {
        ContratoDetalle cd = ContratoDetalle.findById(id)
        BigDecimal monto = new BigDecimal(request.JSON.monto as String)
        switch (request.JSON.tipoConcepto as String) {
            case 'Abono Mensual':
                cd.subtotal = cd.subtotal + monto
                break
            case 'Interés Normal':
                cd.iva = cd.iva + monto
                break
            case 'Moratorios':
                cd.moratorios = cd.moratorios + monto
                break
            case 'Penalizacion':
                cd.penalizacion = cd.penalizacion + monto
                break
            case 'Venta de autos':
                cd.ventaDeAutos = cd.ventaDeAutos + monto
                break
            case 'Aportación a capital':
                cd.aportacionAcapital = cd.aportacionAcapital + monto
                break
        }
        cd.validate()
        cd.save(flush: true)
        respond(cd)
    }
}
