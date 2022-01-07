package mx.saccsa.autopresta

class GenLiq_Ext_Liq implements Serializable{
    Long idLiquidacion
    Long idLiquidacionBanco
    String dominio

    static constraints = {
        idLiquidacion nullable: false, blank: false
        idLiquidacionBanco nullable: false, blank: false
        dominio nullable: false, blank: false
    }

    static mapping = {
        table("GENLIQ_EXT_LIQ")
        version(false)
        id composite: ['idLiquidacion', 'idLiquidacionBanco']
        dominio name: "dominio", column: "dominio"
    }
}
