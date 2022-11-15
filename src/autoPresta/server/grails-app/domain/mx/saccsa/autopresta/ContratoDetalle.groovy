package mx.saccsa.autopresta

class ContratoDetalle {
    Contrato contrato
    Integer parcialidad
    Date fecha
    BigDecimal interes
    BigDecimal monitoreo
    BigDecimal gps
    BigDecimal capital
    BigDecimal subtotal
    BigDecimal iva
    BigDecimal saldoFinal
    Date fechaPago
    String estatus
    Boolean conciliado

    BigDecimal montoConciliado=0.00
    BigDecimal mensualidad=0.00
    BigDecimal moratorios=0.00
    BigDecimal penalizacion=0.00
    BigDecimal ventaDeAutos=0.00
    BigDecimal aportacionAcapital=0.00
    Date fechaPagoCapital
    BigDecimal saldoFinalRestante
    Long diasAtraso=0
    BigDecimal moraoriosCondonados=0.00
    BigDecimal moraoriosPagados=0.00

    static constraints = {
        contrato nullable: false, blank: false
        parcialidad nullable: false, blank: false
        fecha nullable: false, blank: false
        interes nullable: false, blank: false
        monitoreo nullable: false, blank: false
        gps nullable: false, blank: false
        capital nullable: false, blank: false
        subtotal nullable: false, blank: false
        iva nullable: false, blank: false
        saldoFinal nullable: false, blank: false
        fechaPago nullable: true, blank: true
        estatus nullable: true, blank: true
        conciliado nullable: true, blank: true
        mensualidad nullable: true, blank:true
        montoConciliado nullable: true, blank:true
        moratorios nullable: true, blank:true
        penalizacion nullable: true, blank:true
        ventaDeAutos nullable: true, blank:true
        aportacionAcapital nullable: true, blank:true
        fechaPagoCapital nullable: true, blank:true
        saldoFinalRestante nullable: true, blank:true
        diasAtraso nullable: true, blank:true
        moraoriosCondonados nullable: true, blank:true
        moraoriosPagados nullable: true, blank:true
    }

    static mapping = {
        table("ContratoDetalle")
        version(false)
        id generator: 'identity'
        contrato name: 'contrato', column: 'contrato'
        parcialidad name: 'parcialidad', column: 'parcialidad'
        fecha name: 'fecha', column: 'fecha'
        interes name: 'interes', column: 'interes'
        monitoreo name: 'monitoreo', column: 'monitoreo'
        gps name: 'gps', column: 'gps'
        capital name: 'capital', column: 'capital'
        subtotal name: 'subtotal', column: 'subtotal'
        iva name: 'iva', column: 'iva'
        saldoFinal name: 'saldoFinal', column: 'saldoFinal'
        fechaPago name: 'fechaPago', column: 'fechaPago'
        estatus name: 'estatus', column: 'estatus'
        conciliado name: 'conciliado', column: 'conciliado'
        mensualidad name: 'mensualidad', column: 'mensualidad'
        montoConciliado name: 'montoConciliado', column: 'montoConciliado'
        moratorios name: 'moratorios', column: 'moratorios'
        penalizacion name: 'penalizacion', column: 'penalizacion'
        ventaDeAutos name: 'ventaDeAutos', column: 'ventaDeAutos'
        aportacionAcapital name: 'aportacionAcapital', column: 'aportacionAcapital'
        fechaPagoCapital name: 'fechaPagoCapital', column: 'fechaPagoCapital'
        saldoFinalRestante name: 'saldoFinalRestante', column: 'saldoFinalRestante'
        diasAtraso name: 'diasAtraso', column: 'diasAtraso'
        moraoriosCondonados name: 'moraoriosCondonados', column: 'moraoriosCondonados'
        moraoriosPagados name: 'moraoriosPagados', column: 'moraoriosPagados'
    }
}
