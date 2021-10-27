package mx.saccsa.autopresta

class TipoContrato {
    String descripcion
    Long duracion

    static constraints = {
        descripcion nullable: true, blank: true
        duracion nullable: true, blank: true
    }

    static mapping = {
        table('TipoContrato')
        version(false)
        id generator: 'identity'
        descripcion name: 'descripcion', column: 'descripcion'
        duracion name: 'duracion', column: 'duracion'
    }

    static transients = ['descLabel']
    String getDescLabel() {
        descripcion
    }
}
