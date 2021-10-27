package mx.saccsa.autopresta

class Folios {
    String cveTipo
    Long folio = 0l

    static mapping = {
        table name:"FOLIOS"
        id 	column: 'cveTipo', name:'cveTipo', generator:'assigned'
        cveTipo name:'cveTipo', column: 'cveTipo'
        folio name: 'folio', column: 'folio'
    }
    String getId(){
        cveTipo
    }
}
