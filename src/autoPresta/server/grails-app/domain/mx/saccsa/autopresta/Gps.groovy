package mx.saccsa.autopresta

class Gps {
    String nombre

    static constraints = {
        nombre nullable: true, blank: true
    }

    static mapping = {
        table('Gps')
        version(false)
        id generator: 'identity'
        nombre column: 'nombre', name: 'nombre'
    }

    static transients = ['descLabel']
    String getDescLabel() {
        nombre
    }
}
