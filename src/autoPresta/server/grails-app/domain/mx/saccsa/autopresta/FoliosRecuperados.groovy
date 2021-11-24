package mx.saccsa.autopresta

class FoliosRecuperados {
    String cveTipo
    String folio

    static mapping = {
        table name: "FoliosRecuperados"
        id generator : 'identity'
        version(false)
        cveTipo name: 'cveTipo', column: 'cveTipo'
        folio name: 'folio', column: 'folio'
    }
}
