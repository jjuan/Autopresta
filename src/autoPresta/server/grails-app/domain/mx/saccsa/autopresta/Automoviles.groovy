package mx.saccsa.autopresta

class Automoviles {
    Marcas marca
    Modelos modelo
    Integer anio

    static constraints = {
    }

    static mapping = {
        table('Automoviles')
        version(false)
        id generator: 'identity'
        marca column: 'marca', name: 'marca'
        modelo column: 'modelo', name: 'modelo'
        anio column: 'anio', name: 'anio'
    }
}
