package mx.saccsa.autopresta

class C_RegimenFiscal {
    String clave
    String descripcion

    static constraints = {
        clave nullable: false, blank: false
        descripcion nullable: false, blank: false
    }

    static mapping = {
        table("C_RegimenFiscal")
        version(false)
        id name: "clave", column: "clave", generator: "assigned"
        descripcion name: "descripcion", column: "descripcion"
    }

    static transients = ['descLabel']
    String getDescLabel() { descripcion }
}
