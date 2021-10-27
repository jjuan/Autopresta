package mx.saccsa.autopresta

class Sucursales {
    String descripcion
    Regiones region
    String ciudad
    String direccion
    String colonia
    String codigoPostal
    String telefono

    static constraints = {
        descripcion size: 0..300
        ciudad size:0..150, nullable: true, blank: true
        direccion size:0..500, nullable: true, blank: true
        colonia size: 0..200, nullable: true, blank: true
        codigoPostal size: 0..10, nullable: true, blank: true
        telefono size: 0..15, nullable: true, blank: true
    }

    static  mapping = {
        table "SUCURSAL"
        codigoPostal name:"codigoPostal", column: "codigoPostal"
        id generator: 'identity'
    }
    static transients = ['descLabel']
    String getDescLabel(){
        descripcion
    }
}
