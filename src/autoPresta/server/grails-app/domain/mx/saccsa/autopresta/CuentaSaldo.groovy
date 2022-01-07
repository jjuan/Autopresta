package mx.saccsa.autopresta

class CuentaSaldo {
    CuentasBancarias cuenta
    Divisas divisa
    Banco banco
    Date fechaSaldo = new Date()
    Boolean cierre=false
    BigDecimal saldo=BigDecimal.ZERO
    BigDecimal saldoInicial=BigDecimal.ZERO

    static constraints = {
        cuenta nullable: true, blank: true
        divisa nullable: true, blank: true
        banco nullable: true, blank: true
        fechaSaldo nullable: true, blank: true
        cierre nullable: true, blank: true
        saldo nullable: true, blank: true, scale: 2
        saldoInicial nullable: true, blank: true, scale: 2
    }


    static mapping={
        table "CUENTASALDO"
        id generator: 'identity'
        cuenta name: "cuenta", column: "cuenta"
        divisa name: "divisa", column: "divisa"
        banco name: "banco", column: "banco"
        fechaSaldo name: "fechaSaldo", column: "fechaSaldo"
        cierre name: "cierre", column: "cierre"
        saldo name: "saldo", column: "saldo"
        saldoInicial name: "saldoInicial", column: "saldoInicial"
    }
}
