package mx.saccsa.autopresta

class GenLiq_Ext_Det {
    String campoLiq
    GenLiq_Ext_Head genLiqExtHead
    String operador
    Long orden
    String valor

    static constraints = {
        campoLiq nullable: false, blank: false, size: 1..100
        genLiqExtHead nullable: false, blank: false
        operador nullable: false, blank: false, size: 1..50
        orden nullable: false, blank: false
        valor nullable: false, blank: false, size: 1..100
    }

    static mapping = {
        table("GENLIQ_EXT_DET")
        version(false)
        id generator: "identity"
        campoLiq name: "campoLiq", column: "campoLiq"
        genLiqExtHead name: "genLiqExtHead", column: "genLiqExtHead"
        operador name: "operador", column: "operador"
        orden name: "orden", column: "orden"
        valor name: "valor", column: "valor"
    }
}
