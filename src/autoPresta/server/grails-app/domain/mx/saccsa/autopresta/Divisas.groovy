package mx.saccsa.autopresta

class Divisas {
    String clave
    String descripcion
    static constraints = {
        clave nullable: false, blank:false
        descripcion nullable: false, blank:false
    }

    static mapping = {
        table "Divisas"
        version false
        id generator : "identity"
        clave column: "clave", name: "clave"
        descripcion column: "descripcion", name: "descripcion"
    }

    static transients = ['descLabel']
    String getDescLabel(){ descripcion }
}
