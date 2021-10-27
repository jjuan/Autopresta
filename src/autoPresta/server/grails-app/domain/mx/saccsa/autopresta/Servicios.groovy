package mx.saccsa.autopresta

class Servicios {
    Gps gps1
    Proveedores proveedor1
    Gps gps2
    Proveedores proveedor2
    Gps gps3
    Proveedores proveedor3

    static constraints = {
        gps1 nullable: true, blank: true
        proveedor1 nullable: true, blank: true
        gps2 nullable: true, blank: true
        proveedor2 nullable: true, blank: true
        gps3 nullable: true, blank: true
        proveedor3 nullable: true, blank: true
    }

    static mapping = {
        table('Servicios')
        version(false)
        id generator: 'identity'
        gps1 column: 'gps1', name: 'gps1'
        proveedor1 column: 'proveedor1', name: 'proveedor1'
        gps2 column: 'gps2', name: 'gps2'
        proveedor2 column: 'proveedor2', name: 'proveedor2'
        gps3 column: 'gps3', name: 'gps3'
        proveedor3 column: 'proveedor3', name: 'proveedor3'
    }
}
