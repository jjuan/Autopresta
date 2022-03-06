package mx.saccsa.autopresta

import mx.saccsa.security.Usuario

class Conciliaciones {
    Date fechaConciliacion = new Date()
    BigDecimal montoXoperaciones
    BigDecimal montoXmovimientos
    BigDecimal diferencia
    String descripcionDiferencia
    Boolean conciliacionParcial = false

    static constraints = {
        fechaConciliacion nullable: false, blank: false
        montoXoperaciones nullable: false, blank: false
        montoXmovimientos nullable: false, blank: false
        diferencia nullable: false, blank: false
        descripcionDiferencia nullable: false, blank: false
//        usuario nullable: true, blank: true
        conciliacionParcial nullable: true, blank: true
    }

    static mapping = {
        table("CONCILIACION")
        version(false)
        id generator: "identity"
        fechaConciliacion name: "fechaConciliacion", column: "fechaConciliacion"
        montoXoperaciones name: "montoXoperaciones", column: "montoXoperaciones"
        montoXmovimientos name: "montoXmovimientos", column: "montoXmovimientos"
        diferencia name: "diferencia", column: "diferencia"
        descripcionDiferencia name: "descripcionDiferencia", column: "descripcionDiferencia"
//        usuario name: "usuario", column: "usuario"
        conciliacionParcial name: "conciliacionParcial", column: "conciliacionParcial"
    }
}
