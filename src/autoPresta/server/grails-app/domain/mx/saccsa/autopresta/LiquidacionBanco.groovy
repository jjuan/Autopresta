package mx.saccsa.autopresta

class LiquidacionBanco {
    String nombreArchivo
    Banco banco
    String cuenta
    Date fecha
    Date fechaOperacion
    String referencia
    String afavor
    String descripcion
    String codigo
    String sucursal
    Boolean cargoAbono //Abono = true
    BigDecimal monto
    BigDecimal montoAbono
    BigDecimal montoCargo
    BigDecimal saldo
    String noMovimiento
    String cheque
    //Campos Adicionales Banamex
    String moneda
    String leyenda1
    String numeroBaseCliente
    String nombreBaseCliente
    String numeroSucursal
    Date fechaEstadoEfectivo
    BigDecimal saldoContableDispo
    BigDecimal numeroCreditos
    BigDecimal montoTotalCredito
    BigDecimal numeroDebitos
    BigDecimal montoTotalDebito
    BigDecimal montoNeto
    String referenciaCliente
    String nombreBanco
    String nombreSucursal
    String tipoCuenta
    BigDecimal saldoContableActual
    BigDecimal saldoInicialCon
    BigDecimal saldoDispoActual
    Long liquidacionId
    String estatus = "PE"
    String proceso
    String comentario
    Date fechaConfirmacion
    String usuarioConfirmacion
    Date fechaCorreccion
    String usuarioCorreccion
    Boolean esSBC
    String estatusCruce = "NA"
    Boolean cA
    String cuentaSaldo

    Boolean esSaldo = Boolean.FALSE
    Date fechaAlta
    Boolean conciliado = false
    static constraints = {
        nombreArchivo size:0..200
        cuenta nullable:true, blank:true, size: 0..100
        fechaOperacion nullable:true, blank:true
        fechaEstadoEfectivo nullable:true, blank:true
        referencia nullable:true, blank:true, size: 0..500
        afavor nullable:true, blank:true, size: 0..400
        descripcion nullable:true, blank:true, size: 0..400
        codigo nullable:true, blank:true, size: 0..100
        sucursal nullable:true, blank:true, size: 0..200
        montoAbono nullable:true, blank:true, scale: 4
        montoCargo nullable:true, blank:true, scale: 4
        saldo nullable:true, blank:true, scale: 4
        noMovimiento nullable: true, blank: true, size: 0..100
        cheque nullable:true, blank:true, size: 0..100
        monto nullable: true, scale: 4
        cargoAbono nullable: true
        liquidacionId nullable: true
        estatus size: 0..10, nullable: true, blank: true
        moneda nullable:true, blank:true, size: 0..20
        leyenda1 nullable:true, blank:true, size: 0..500
        numeroBaseCliente nullable:true, blank:true, size: 0..200
        nombreBaseCliente nullable:true, blank:true, size: 0..200
        numeroSucursal nullable:true, blank:true, size: 0..100
        saldoContableDispo nullable:true, blank:true, scale: 4
        numeroCreditos nullable:true, blank:true, scale: 4
        montoTotalCredito nullable:true, blank:true, scale: 4
        numeroDebitos nullable:true, blank:true, scale: 4
        montoTotalDebito nullable:true, blank:true, scale: 4
        montoNeto nullable:true, blank:true, scale: 4
        referenciaCliente nullable:true, blank:true, size: 0..200
        nombreBanco nullable:true, blank:true, size: 0..100
        nombreSucursal nullable:true, blank:true, size: 0..100
        tipoCuenta nullable:true, blank:true, size: 0..100
        saldoContableActual nullable:true, blank:true, scale: 4
        saldoInicialCon nullable:true, blank:true, scale: 4
        saldoDispoActual nullable:true, blank:true, scale: 4
        proceso size:0..100
        comentario nullable:true, blank:true, size: 0..150
        fechaConfirmacion nullable:true, blank:true
        usuarioConfirmacion nullable:true, blank:true, size: 0..50
        fechaCorreccion nullable:true, blank:true
        usuarioCorreccion nullable:true, blank:true, size: 0..50
        esSBC nullable: true, blank: true
        estatusCruce size: 0..10, nullable: true, blank: true
        cuentaSaldo nullable:true, blank:true, size: 0..100
        cA nullable: true
        fechaAlta nullable: true, blank: true
        conciliado nullable: true
    }
    static mapping = {
        table("LIQUIDACION_BANCO")
        version(false)
        id generator: "identity"
        nombreArchivo name:"nombreArchivo", column:"nombreArchivo"
        banco name:"banco", column:"banco"
        cuenta name:"cuenta", column:"cuenta"
        fecha name:"fecha", column:"fecha"
        fechaOperacion name:"fechaOperacion", column:"fechaOperacion"
        referencia name:"referencia", column:"referencia"
        afavor name:"afavor", column:"afavor"
        descripcion name:"descripcion", column:"descripcion"
        codigo name:"codigo", column:"codigo"
        sucursal name:"sucursal", column:"sucursal"
        montoAbono name:"montoAbono", column:"montoAbono"
        montoCargo name:"montoCargo", column:"montoCargo"
        saldo name:"saldo", column:"saldo"
        noMovimiento name:"noMovimiento", column: "noMovimiento"
        cheque name:"cheque", column:"cheque"
        monto name:"monto", column: "monto"
        cargoAbono name:"cargoAbono", column: "cargoAbono"
        liquidacionId name:"liquidacionId", column:"liquidacionId"
        estatus name:"estatus", column: "estatus"
        moneda name:"moneda", column:"moneda"
        leyenda1 name:"leyenda1", column:"leyenda1"
        numeroBaseCliente name:"numeroBaseCliente", column:"numeroBaseCliente"
        nombreBaseCliente name:"nombreBaseCliente", column:"nombreBaseCliente"
        numeroSucursal name:"numeroSucursal", column:"numeroSucursal"
        saldoContableDispo name:"saldoContableDispo", column:"saldoContableDispo"
        numeroCreditos name:"numeroCreditos", column:"numeroCreditos"
        montoTotalCredito name:"montoTotalCredito", column:"montoTotalCredito"
        numeroDebitos name:"numeroDebitos", column:"numeroDebitos"
        montoTotalDebito name:"montoTotalDebito", column:"montoTotalDebito"
        montoNeto name:"montoNeto", column:"montoNeto"
        referenciaCliente name:"referenciaCliente", column:"referenciaCliente"
        nombreBanco name:"nombreBanco", column:"nombreBanco"
        nombreSucursal name:"nombreSucursal", column:"nombreSucursal"
        tipoCuenta name:"tipoCuenta", column:"tipoCuenta"
        saldoContableActual name:"saldoContableActual", column:"saldoContableActual"
        saldoInicialCon name:"saldoInicialCon", column:"saldoInicialCon"
        saldoDispoActual name:"saldoDispoActual", column:"saldoDispoActual"
        fechaEstadoEfectivo name:"fechaEstadoEfectivo",column: "fechaEstadoEfectivo"
        proceso name:"proceso", column: "proceso"
        comentario name: 'comentario', column: 'comentario'
        fechaConfirmacion name: 'fechaConfirmacion', column: 'fechaConfirmacion'
        usuarioConfirmacion name: 'usuarioConfirmacion', column: 'usuarioConfirmacion'
        fechaCorreccion name: 'fechaCorreccion', column: 'fechaCorreccion'
        usuarioCorreccion name: 'usuarioCorreccion', column: 'usuarioCorreccion'
        esSBC name: 'esSBC', column: 'esSBC'
        estatusCruce name: 'estatusCruce', column: 'estatusCruce'
        cuentaSaldo name: 'cuentaSaldo', column: 'cuentaSaldo'
        cA name: 'cA', column: 'cA'
        esSaldo name:"esSaldo", column:"esSaldo"
        fechaAlta name:"fechaAlta", column:"fechaAlta"
        conciliado name:"conciliado", column:"conciliado"
    }

}
