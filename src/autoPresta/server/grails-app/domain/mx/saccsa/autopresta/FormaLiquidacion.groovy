package mx.saccsa.autopresta

class FormaLiquidacion {
    Banco bancoCasa
    CuentasBancarias chequeraCasa
    String descripcion
    Divisas divisa
    String  estatusAplicacion
    String formaLiquidacion
    BigDecimal montoMax = 0
    BigDecimal montoMin = 0
    Boolean multibanco = false
    Boolean numeroCheque = false
    Boolean pBanco = false
    Boolean pBuscaConcentradora = false
    Boolean pChequera = false
    Boolean pDireccion = false
    String preguntaImpresion
    String tipoAplicacion
    String tipoImpresion
    String tipoMovimiento

    static constraints = {
        bancoCasa nullable: true, blank: true
        chequeraCasa nullable: true, blank: true
        descripcion nullable: true, blank: true
        divisa nullable: true, blank: true
        estatusAplicacion nullable: true, blank: true
        formaLiquidacion nullable: true, blank: true
        montoMax nullable: true, blank: true
        montoMin nullable: true, blank: true
        multibanco nullable: true, blank: true
        numeroCheque nullable: true, blank: true
        pBanco nullable: true, blank: true
        pBuscaConcentradora nullable: true, blank: true
        pChequera nullable: true, blank: true
        pDireccion nullable: true, blank: true
        preguntaImpresion nullable: true, blank: true
        tipoAplicacion nullable: true, blank: true
        tipoImpresion nullable: true, blank: true
        tipoMovimiento nullable: true, blank: true
    }

    static mapping = {
        table("FORMALIQUIDACION")
        version(false)
        id generator: "identity"
        bancoCasa name: "bancoCasa", column: "bancoCasa"
        chequeraCasa name: "chequeraCasa", column: "chequeraCasa"
        descripcion name: "descripcion", column: "descripcion"
        divisa name: "divisa", column: "divisa"
        estatusAplicacion name: "estatusAplicacion", column: "estatusAplicacion"
        formaLiquidacion name: "formaLiquidacion", column: "formaLiquidacion"
        montoMax name: "montoMax", column: "montoMax"
        montoMin name: "montoMin", column: "montoMin"
        multibanco name: "multibanco", column: "multibanco"
        numeroCheque name: "numeroCheque", column: "numeroCheque"
        pBanco name: "pBanco", column: "pBanco"
        pBuscaConcentradora name: "pBuscaConcentradora", column: "pBuscaConcentradora"
        pChequera name: "pChequera", column: "pChequera"
        pDireccion name: "pDireccion", column: "pDireccion"
        preguntaImpresion name: "preguntaImpresion", column: "preguntaImpresion"
        tipoAplicacion name: "tipoAplicacion", column: "tipoAplicacion"
        tipoImpresion name: "tipoImpresion", column: "tipoImpresion"
        tipoMovimiento name: "tipoMovimiento", column: "tipoMovimiento"
    }

    static transients = ['descLabel']
    String getDescLabel() { descripcion }
}
