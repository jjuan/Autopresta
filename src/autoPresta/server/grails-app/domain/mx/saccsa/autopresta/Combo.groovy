package mx.saccsa.autopresta

class Combo  implements Serializable{
    String combo
    String clave
    String descripcion
    Boolean habilitado = true
    Long orden
    String tipo ='S'
    static constraints = {
        combo size:1..50
        clave size:1..20
        descripcion size: 1..200
        orden nullable: true
        tipo size: 1..100
    }

    static mapping = {
        table "COMBO"
        id composite: ['combo', 'clave']
        version false
    }
    static descripcion(String combo,String clave) {
        findByComboAndClave(combo, clave)?.descripcion ?: clave
    }
}
