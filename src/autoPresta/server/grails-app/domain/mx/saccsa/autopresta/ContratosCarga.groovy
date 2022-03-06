package mx.saccsa.autopresta

class ContratosCarga {
    String numeroContrato
    String referenciaBancaria
    Date fechaOtorgamiento
    String rfc
    String nombreDelCliente
    String representanteLegal
    String marcaDelAuto
    String modelo
    String anio
    String color
    String placas
    String numeroVin
    String rango
    Boolean capturado = false
    BigDecimal montoPrestado
    BigDecimal interes
    BigDecimal gps
    BigDecimal monitoreo
    BigDecimal total
    BigDecimal iva
    BigDecimal granTotal
    Date fechaCorte


    static constraints = {
        numeroContrato nullable: true
        referenciaBancaria nullable: true
        fechaOtorgamiento nullable: true
        rfc nullable: true
        nombreDelCliente nullable: true
        representanteLegal nullable: true
        marcaDelAuto nullable: true
        modelo nullable: true
        anio nullable: true
        color nullable: true
        placas nullable: true
        numeroVin nullable: true
        montoPrestado nullable: true
        interes nullable: true
        gps nullable: true
        monitoreo nullable: true
        total nullable: true
        iva nullable: true
        granTotal nullable: true
        fechaCorte nullable: true
        rango nullable: true
        capturado nullable: true
    }

    static mapping = {
        table('ContratosCarga')
        version(false)
        numeroContrato column: 'numeroContrato', name: 'numeroContrato'
        referenciaBancaria column: 'referenciaBancaria', name: 'referenciaBancaria'
        fechaOtorgamiento column: 'fechaOtorgamiento', name: 'fechaOtorgamiento'
        rfc column: 'rfc', name: 'rfc'
        nombreDelCliente column: 'nombreDelCliente', name: 'nombreDelCliente'
        representanteLegal column: 'representanteLegal', name: 'representanteLegal'
        marcaDelAuto column: 'marcaDelAuto', name: 'marcaDelAuto'
        modelo column: 'modelo', name: 'modelo'
        anio column: 'anio', name: 'anio'
        color column: 'color', name: 'color'
        placas column: 'placas', name: 'placas'
        numeroVin column: 'numeroVin', name: 'numeroVin'
        montoPrestado column: 'montoPrestado', name: 'montoPrestado'
        interes column: 'interes', name: 'interes'
        gps column: 'gps', name: 'gps'
        monitoreo column: 'monitoreo', name: 'monitoreo'
        total column: 'total', name: 'total'
        iva column: 'iva', name: 'iva'
        granTotal column: 'granTotal', name: 'granTotal'
        fechaCorte column: 'fechaCorte', name: 'fechaCorte'
        rango name: 'rango', column: 'rango'
        capturado name: 'capturado', column: 'capturado'
    }
}
