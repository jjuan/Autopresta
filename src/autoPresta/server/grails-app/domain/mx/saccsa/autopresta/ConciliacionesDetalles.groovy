package mx.saccsa.autopresta

import mx.saccsa.security.Usuario

class ConciliacionesDetalles {
    Conciliaciones conciliaciones
    LiquidacionBanco movimiento
    String folioOperacion
    String tipoOperacion
    Date fecha
    Usuario usuario
    HojaConciliacion hojaConciliacion
    String formaConciliacion
    String referenciaBancaria
    String cuenta

    static constraints = {
        conciliaciones nullable: false, blank: false
        movimiento nullable: false, blank: false
        folioOperacion nullable: false, blank: false
        tipoOperacion nullable: false, blank: false
        hojaConciliacion nullable: true, blank: true
        formaConciliacion nullable: true, blank: true
        referenciaBancaria nullable: true, blank: true
        cuenta nullable: true, blank: true
    }

    static mapping = {
        table("CONCILIACIONDETALLE")
        version(false)
        id generator: "identity"
        conciliaciones name: "conciliaciones", column: "conciliaciones"
        movimiento name: "movimiento", column: "movimiento"
        folioOperacion name: "folioOperacion", column: "folioOperacion"
        tipoOperacion name: "tipoOperacion", column: "tipoOperacion"
        hojaConciliacion name: "hojaConciliacion", column: "hojaConciliacion"
        formaConciliacion name: "formaConciliacion", column: "formaConciliacion"
        referenciaBancaria name: "referenciaBancaria", column: "referenciaBancaria"
        cuenta name: "cuenta", column: "cuenta"
    }
}
