package mx.saccsa.autopresta

class Agencias {
    String nombre
    Sucursales sucursal
    Regiones region
    Portafolios portafolio
    String clave

    static constraints = {
        nombre nullable: true, blank: true
        sucursal nullable: true, blank: true
        region nullable: true, blank: true
        portafolio nullable: true, blank: true
        clave nullable: true, blank: true
    }

    static mapping = {
        table('Agencias')
        version(false)
        id generator: 'identity'
        nombre column: 'nombre', name: 'nombre'
        sucursal column: 'sucursal', name: 'sucursal'
        region column: 'region', name: 'region'
        portafolio column: 'portafolio', name: 'portafolio'
        clave column: 'clave', name: 'clave'
    }

    static transients = ['descLabel']
    String getDescLabel() {
        nombre
    }
}
