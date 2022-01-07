package mx.saccsa.autopresta

class GenLiq_Ext_Map {
    String campoLiqBanco
    String campoLiquidacion
    GenLiq_Ext_Head genLiqExtHead

    static constraints = {
        campoLiqBanco nullable: false, blank: false
        campoLiquidacion nullable: false, blank: false
        genLiqExtHead nullable: false, blank: false
    }


    static mapping = {
        table("GENLIQ_EXT_MAP")
        version(false)
        id generator: "identity"
        campoLiqBanco name: "campoLiqBanco", column: "campoLiqBanco"
        campoLiquidacion name: "campoLiquidacion", column: "campoLiquidacion"
        genLiqExtHead name: "genLiqExtHead", column: "genLiqExtHead"
    }
}
