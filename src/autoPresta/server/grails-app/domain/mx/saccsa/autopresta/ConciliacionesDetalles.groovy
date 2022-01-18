package mx.saccsa.autopresta

class ConciliacionesDetalles {
    Conciliaciones conciliaciones
    LiquidacionBanco movimiento
    String folioOperacion
    String tipoOperacion

    static constraints = {
        conciliaciones nullable: false, blank: false
        movimiento nullable: false, blank: false
        folioOperacion nullable: false, blank: false
        tipoOperacion nullable: false, blank: false
    }

    static mapping = {
        table("CONCILIACIONDETALLE")
        version(false)
        id generator: "identity"
        conciliaciones name: "conciliaciones", column: "conciliaciones"
        movimiento name: "movimiento", column: "movimiento"
        folioOperacion name: "folioOperacion", column: "folioOperacion"
        tipoOperacion name: "tipoOperacion", column: "tipoOperacion"
    }
}
