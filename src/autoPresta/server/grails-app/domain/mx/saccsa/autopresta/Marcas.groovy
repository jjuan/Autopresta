package mx.saccsa.autopresta

class Marcas {
    String nombre
    String slug

    static constraints = {
        nombre nullable: true, blank: true
    }

    static mapping = {
        table('Marcas')
        version(false)
        id generator: 'identity'
        nombre column: 'nombre', name: 'nombre'
        slug column: 'slug', name: 'slug'
    }

    static transients = ['descLabel']
    String getDescLabel() { nombre }
}
