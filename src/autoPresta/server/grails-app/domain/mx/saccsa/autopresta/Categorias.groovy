package mx.saccsa.autopresta

class Categorias {
    String descripcion

    static constraints = {
        descripcion nullable: false, blank: false
    }

    static mapping = {
        table("CATEGORIAS")
        version(false)
        id generator: "identity"
        descripcion name: "descripcion", column: "descripcion"
    }

    static transients = ['descLabel']
    String getDescLabel() { descripcion }
}
