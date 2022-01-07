package mx.saccsa.autopresta

class CuentasBancarias {
    Portafolios razonSocial
    Banco banco
    String alias
    String cuenta
    String clabe
    Date fechaDeApertura
    String estatus
    Date fechaDeCancelacion
    Divisas moneda

    static constraints = {
        razonSocial nullable: false, blank: false
        banco nullable: false, blank: false
        alias nullable: false, blank: false
        cuenta nullable: false, blank: false
        clabe nullable: false, blank: false
        moneda nullable: false, blank: false
        fechaDeApertura nullable: true, blank: true
        estatus nullable: true, blank: true
        fechaDeCancelacion nullable: true, blank: true
    }

    static mapping = {
        table "CUENTASBANCARIAS"
        version false
        razonSocial column: "razonSocial", name: "razonSocial"
        banco column: "banco", name: "banco"
        alias column: "alias", name: "alias"
        cuenta column: "cuenta", name: "cuenta"
        clabe column: "clabe", name: "clabe"
        moneda column: "moneda", name: "moneda"
        fechaDeApertura column: "fechaDeApertura", name: "fechaDeApertura"
        estatus column: "estatus", name: "estatus"
        fechaDeCancelacion column: "fechaDeCancelacion", name: "fechaDeCancelacion"
    }

    static transients = ['descLabel']
    String getDescLabel(){ alias }
}
