package mx.saccsa.autopresta

class GenLiq_Campos {
    String campo
    String descripcion
    String tabla
    String tipo

    static constraints = {
        campo nullable: false, blank: false, size: 1..100
        descripcion nullable: false, blank: false, size: 1..100
        tabla nullable: false, blank: false, size: 1..100
        tipo nullable: false, blank: false, size: 1..50
    }

    static mapping = {
        table("GENLIQ_CAMPOS")
        version(false)
        id generator: "identity"
        campo name: "campo", column: "campo"
        descripcion name: "descripcion", column: "descripcion"
        tabla name: "tabla", column: "tabla"
        tipo name: "tipo", column: "tipo"
    }
}
