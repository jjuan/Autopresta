package mx.saccsa.autopresta

class Proveedores {
    //RazonSocial razonSocial
    String nombre
    String rfc
    Divisas moneda
    Divisas monedaFactura
    String nombreDeContacto
    String correoElectronico
    String direccion
    String telefono
    String estatus
    String tipo

    //datos del pdf
   /* String calle
    String noExterior
    String noInterior
    String colonia
    String codigoPostal
    String localidad
    String municipio
    String estado
    String pais*/

    static constraints = {
        //razonSocial nullable: true, blank: true
        nombre nullable: true, blank: true
        rfc nullable: true, blank: true
        moneda nullable: true, blank: true
        monedaFactura nullable: true, blank: true
        nombreDeContacto nullable: true, blank: true
        correoElectronico nullable: true, blank: true
        direccion nullable: true, blank: true
        telefono nullable: true, blank: true
        estatus nullable: true, blank: true
        tipo nullable: true, blank: true

        /*calle nullable: true, blank: true
        noExterior nullable: true, blank: true
        noInterior nullable: true, blank: true
        colonia nullable: true, blank: true
        codigoPostal nullable: true, blank: true
        localidad nullable: true, blank: true
        municipio nullable: true, blank: true
        estado nullable: true, blank: true
        pais nullable: true, blank: true*/
    }

    static mapping = {
        table "PROVEEDORES"
        version false
        id generator : "identity"
        //razonSocial column: "razonSocial", name: "razonSocial"
        nombre column: "nombre", name: "nombre"
        rfc column: "rfc", name: "rfc"
        moneda column: "moneda", name: "moneda"
        monedaFactura column: "monedaFactura", name: "monedaFactura"
        nombreDeContacto name: "nombreDeContacto", column: "nombreDeContacto"
        correoElectronico name: "correoElectronico", column: "correoElectronico"
        direccion column: "direccion", name: "direccion"
        telefono column: "telefono", name: "telefono"
        estatus column: "estatus", name: "estatus"
        tipo column: "tipo", name: "tipo"

        /*calle name: "calle", column: "calle"
        noExterior name: "noExterior", column: "noExterior"
        noInterior name: "noInterior", column: "noInterior"
        colonia name: "colonia", column: "colonia"
        codigoPostal name: "codigoPostal", column: "codigoPostal"
        localidad name: "localidad", column: "localidad"
        municipio name: "municipio", column: "municipio"
        estado name: "estado", column: "estado"
        pais name: "pais", column: "pais"*/
    }

    static transients = ['descLabel']
    String getDescLabel(){ nombre }
}
