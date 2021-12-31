package mx.saccsa.autopresta

class IdentificacionesOficiales {
    String nombre
    Integer longitud

    static constraints = {
        nombre nullable: true, blank: true
        longitud nullable: true, blank: true
    }

    static mapping = {
        table('IdentificacionesOficiales')
        version(false)
        id generator: 'identity'
        nombre name: 'nombre', column: 'nombre'
        longitud name: 'longitud', column: 'longitud'
    }

    static transients = ['descLabel']
    String getDescLabel() {
        nombre
    }
    static getNombreById(String id){
        IdentificacionesOficiales.findById(id)?.nombre
    }
}
