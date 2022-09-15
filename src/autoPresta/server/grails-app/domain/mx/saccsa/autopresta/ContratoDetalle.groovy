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
    BigDecimal interesReal
    BigDecimal monitoreoReal
    BigDecimal gpsReal
    BigDecimal capitalReal
    BigDecimal subtotalReal
    BigDecimal ivaReal
    BigDecimal saldoFinalReal
    BigDecimal montoConciliado=0.00
    BigDecimal moratorios
    BigDecimal penalizacion
    BigDecimal ventaDeAutos
    BigDecimal aportacionAcapital

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
        interesReal nullable: true, blank:true
        monitoreoReal nullable: true, blank:true
        gpsReal nullable: true, blank:true
        capitalReal nullable: true, blank:true
        subtotalReal nullable: true, blank:true
        ivaReal nullable: true, blank:true
        saldoFinalReal nullable: true, blank:true
        montoConciliado nullable: true, blank:true
        moratorios nullable: true, blank:true
        penalizacion nullable: true, blank:true
        ventaDeAutos nullable: true, blank:true
        aportacionAcapital nullable: true, blank:true
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
        interesReal name: 'interesReal', column: 'interesReal'
        monitoreoReal name: 'monitoreoReal', column: 'monitoreoReal'
        gpsReal name: 'gpsReal', column: 'gpsReal'
        capitalReal name: 'capitalReal', column: 'capitalReal'
        subtotalReal name: 'subtotalReal', column: 'subtotalReal'
        ivaReal name: 'ivaReal', column: 'ivaReal'
        saldoFinalReal name: 'saldoFinalReal', column: 'saldoFinalReal'
        montoConciliado name: 'montoConciliado', column: 'montoConciliado'
        moratorios name: 'moratorios', column: 'moratorios'
        penalizacion name: 'penalizacion', column: 'penalizacion'
        ventaDeAutos name: 'ventaDeAutos', column: 'ventaDeAutos'
        aportacionAcapital name: 'aportacionAcapital', column: 'aportacionAcapital'
    }
}
