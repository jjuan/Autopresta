package mx.saccsa.autopresta

class Subconceptos {
    String subconcepto
    String descripcion
    String concepto
    RazonesSociales razonSocial
    Divisas divisa
    FormaLiquidacion formaLiquidacion
    Categorias categoria

    static constraints = {
        subconcepto nullable: false, blank: false
        descripcion nullable: false, blank: false
        concepto nullable: false, blank: false
        razonSocial nullable: false, blank: false
        divisa nullable: false, blank: false
        formaLiquidacion nullable: false, blank: false
        categoria nullable: false, blank: false
    }

    static mapping = {
        table("SUBCONCEPTOS")
        version(false)
        id generator: "identity"
        subconcepto name: "subconcepto", column: "subconcepto"
        descripcion name: "descripcion", column: "descripcion"
        concepto name: "concepto", column: "concepto"
        razonSocial name: "razonSocial", column: "razonSocial"
        divisa name: "divisa", column: "divisa"
        formaLiquidacion name: "formaLiquidacion", column: "formaLiquidacion"
        categoria name: "categoria", column: "categoria"
    }

    static transients = ['descLabel']
    String getDescLabel() { descripcion }
}
