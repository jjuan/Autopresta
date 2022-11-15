package mx.saccsa.autopresta

class HojaConciliacion {
    Contrato folio
    String regla1
    String regla2
    String regla3
    String regla4
    String regla5
    String regla6
    String regla7
    String regla8

    static constraints = {
        folio nullable: false, blank: false
        regla1 nullable: true, blank: true
        regla2 nullable: true, blank: true
        regla3 nullable: true, blank: true
        regla4 nullable: true, blank: true
        regla5 nullable: true, blank: true
        regla6 nullable: true, blank: true
        regla7 nullable: true, blank: true
        regla8 nullable: true, blank: true
    }

    static mapping = {
        table('HojaConciliacion')
        id generator: 'identity'
        folio name: 'folio', column: 'folio'
        regla1 name: 'regla1', column: 'regla1'
        regla2 name: 'regla2', column: 'regla2'
        regla3 name: 'regla3', column: 'regla3'
        regla4 name: 'regla4', column: 'regla4'
        regla5 name: 'regla5', column: 'regla5'
        regla6 name: 'regla6', column: 'regla6'
        regla7 name: 'regla7', column: 'regla7'
        regla8 name: 'regla8', column: 'regla8'
    }
}
