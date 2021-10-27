package mx.saccsa.autopresta

class Marcas {
    Agencias agencia
    String nombre

    static constraints = {
        agencia nullable: true, blank: true
        nombre nullable: true, blank: true
    }

    static mapping = {
        table('Marcas')
        version(false)
        id generator: 'identity'
        agencia column: 'agencia', name: 'agencia'
        nombre column: 'nombre', name: 'nombre'
    }

    static transients = ['descLabel']
    String getDescLabel() { nombre }
}
