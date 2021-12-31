package mx.saccsa.autopresta

class ContratoDetalle {
    Contrato contrato
    String parcialidad
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
    }
}
