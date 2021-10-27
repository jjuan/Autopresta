package mx.saccsa.autopresta

class Modelos {
    String nombre

    static constraints = {
        nombre nullable: true, blank: true
    }

    static mapping = {
        table('Modelos')
        version(false)
        id generator: 'identity'
        nombre column: 'nombre', name: 'nombre'
    }

    static transients = ['descLabel']
    String getDescLabel() {
        nombre
    }
}
