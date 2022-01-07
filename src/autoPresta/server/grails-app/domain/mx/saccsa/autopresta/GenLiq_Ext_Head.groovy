package mx.saccsa.autopresta

class GenLiq_Ext_Head {
    Banco banco
    CuentasBancarias chequerasCasa
    String configuracion
    Boolean habilitado
    String nombre
    Long orden
    Subconceptos subconcepto
    String tipoArchivo
    String cargoAbono

    static constraints = {
        banco nullable: false, blank: false
        chequerasCasa nullable: false, blank: false
        configuracion nullable: true, blank: true, size: 1..100
        habilitado nullable: false, blank: false
        nombre nullable: false, blank: false, size: 1..80
        orden nullable: false, blank: false
        subconcepto nullable: false, blank: false
        tipoArchivo nullable: false, blank: false, size: 1..80
        cargoAbono nullable: false, blank: false, size: 1..1
    }


    static mapping = {
        table("GENLIQ_EXT_HEAD")
        version(false)
        id generator: "identity"
        banco name: "banco", column: "banco"
        chequerasCasa name: "chequerasCasa", column: "chequerasCasa"
        configuracion name: "configuracion", column: "configuracion"
        habilitado name: "habilitado", column: "habilitado"
        nombre name: "nombre", column: "nombre"
        orden name: "orden", column: "orden"
        subconcepto name: "subconcepto", column: "subconcepto"
        tipoArchivo name: "tipoArchivo", column: "tipoArchivo"
        cargoAbono name: "cargoAbono", column: "cargoAbono"
    }
}
