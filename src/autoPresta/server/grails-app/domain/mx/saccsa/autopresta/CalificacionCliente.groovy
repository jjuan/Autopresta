package mx.saccsa.autopresta

class CalificacionCliente {
    String nombre
    String descripcion

    static constraints = {
        nombre nullable: true, blank: true
        descripcion nullable: true, blank: true
    }

    static mapping = {
        table('CalificacionCliente')
        version(false)
        id generator: 'identity'
        nombre column: 'nombre', name: 'nombre'
        descripcion column: 'descripcion', name: 'descripcion'
    }

    static transients = ['descLabel']
    String getDescLabel() { nombre }
}
