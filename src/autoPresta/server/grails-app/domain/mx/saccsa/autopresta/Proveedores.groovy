package mx.saccsa.autopresta

class Proveedores {
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

    static constraints = {
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
    }

    static mapping = {
        table "Proveedores"
        version false
        id generator : "identity"
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
    }

    static transients = ['descLabel']
    String getDescLabel(){ nombre }
}
