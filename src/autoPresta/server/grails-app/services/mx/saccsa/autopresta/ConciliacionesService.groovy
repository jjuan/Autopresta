package mx.saccsa.autopresta

import grails.gorm.transactions.Transactional

//
@Transactional
class ConciliacionesService {
    def reporteService

    def cargarMovimientos(String cuenta, Boolean cargoAbono, Date fechaInicio, Date fechaFin, Boolean... conciliados) {
        def lista
        if (conciliados) {
            lista = LiquidacionBanco.findAllByCuentaAndCargoAbonoAndConciliadoAndFechaBetween(cuenta, cargoAbono, conciliados[0] as Boolean, fechaInicio, fechaFin)
        } else {
            lista = LiquidacionBanco.findAllByCuentaAndCargoAbonoAndFechaBetween(cuenta, cargoAbono, fechaInicio, fechaFin)
        }
        lista = lista.collect({
            [
                    folio     : it.id,
                    cuenta    : it.cuenta,
                    fecha     : it.fecha,
                    referencia: it.referencia,
                    monto     : it.monto,
                    estatus   : it.conciliado ? 'Conciliado' : 'No conciliado'
            ]
        })
        return lista
    }

    def cargarParcialidades(Date fechaInicio, Date fechaFin, Boolean... conciliados) {
        def lista
        def contratosExcluidos = Contrato.findAllByContratoPruebaOrNumeroContrato(true, '')
        if (conciliados) {
            lista = ContratoDetalle.findAllByContratoNotInListAndConciliadoAndFechaBetween(contratosExcluidos, conciliados[0] as Boolean, fechaInicio, fechaFin)
        } else {
            lista = ContratoDetalle.findAllByContratoNotInListAndFechaBetween(contratosExcluidos, fechaInicio, fechaFin)
        }
        lista = lista.collect({
            [
                    folio      : it.id,
                    contrato   : reporteService.contratoFolio(it.contrato),
                    parcialidad: it.parcialidad,
                    fecha      : it.fecha,
                    monto      : it.subtotal + it.iva,
                    estatus    : it.conciliado ? 'Conciliado' : 'No conciliado'
            ]
        })
        return lista
    }
}
