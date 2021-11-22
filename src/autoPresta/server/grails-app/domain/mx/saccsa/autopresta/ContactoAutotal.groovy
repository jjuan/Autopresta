package mx.saccsa.autopresta

class ContactoAutotal {
    Integer year
    String marca
    String modelo

    static constraints = {
        marca size: 1..150
        modelo size: 1..150
    }

    static mapping = {
        table('ContactoAutotal')
        version(false)
        year name: 'Year', column: 'Year'
        marca name: 'Marca', column: 'Marca'
        modelo name: 'Modelo', column: 'Modelo'
    }
}
