package mx.saccsa.autopresta

class Modelos {
    Marcas marca
    String nombre
    String slug

    static constraints = {
        marca nullable: true, blank: true
        nombre nullable: true, blank: true
        slug nullable: true, blank: true
    }

    static mapping = {
        table('Modelos')
        version(false)
        id generator: 'identity'
        marca column: 'marca', name: 'marca'
        nombre column: 'nombre', name: 'nombre'
        slug column: 'slug', name: 'slug'
    }

    static transients = ['descLabel']
    String getDescLabel() {
        nombre
    }
}
