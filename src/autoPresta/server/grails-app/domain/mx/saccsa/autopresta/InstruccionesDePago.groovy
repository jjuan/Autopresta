package mx.saccsa.autopresta

class InstruccionesDePago {
    Proveedores proveedor
    Banco banco
    String cuenta
    String clabe
    Divisas moneda
    String convenio
    String referencia
    String concepto

    static constraints = {
        proveedor nullable: false, blank: false
        banco nullable: true, blank: true
        cuenta nullable: true, blank: true
        clabe nullable: true, blank: true
        moneda nullable: true, blank: true
        convenio nullable: true, blank: true
        referencia nullable: true, blank: true
        concepto nullable: true, blank: true
    }

    static mapping = {
        table "INSTRUCCIONESDEPAGO"
        version false
        id generator : "identity"
        proveedor name: "proveedor", column: "proveedor"
        banco name: "banco", column: "banco"
        cuenta name: "cuenta", column: "cuenta"
        clabe name: "clabe", column: "clabe"
        moneda name: "moneda", column: "moneda"
        convenio name: "convenio", column: "convenio"
        referencia name: "referencia", column: "referencia"
        concepto name: "concepto", column: "concepto"
    }

    static transients = ['descLabel']
    String getDescLabel(){ proveedor.nombre + ' - ' + moneda.clave }
}
