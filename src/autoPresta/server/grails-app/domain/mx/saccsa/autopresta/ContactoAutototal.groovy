package mx.saccsa.autopresta

class ContactoAutototal {
    Integer year
    String marca
    String modelo

    static constraints = {
        marca size: 1..150
        modelo size: 1..150
    }

    static mapping = {
        table('ContactoAutototal')
        version(false)
        year name: 'Year', column: 'Year'
        marca name: 'Marca', column: 'Marca'
        modelo name: 'Modelo', column: 'Modelo'
    }
}
