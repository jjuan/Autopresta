package mx.saccsa.autopresta

class FechaInhabil {
    static auditable =  true
    transient static def headers=['Fecha','Descripcion']
    transient static def withProperties=['fecha','divisa.descripcion']

    Date fecha
    String descripcion
    Divisas divisa

    static constraints = {
        fecha nullable: false
        descripcion nullable:true,blank:true,size:0..500
        divisa nullable: false

    }
    static mapping = {
        table "FECHAINHABIL"
//        id(generator: "sequence",params:['sequence':'FECHAINHABIL_SEQ'])
        id generator: 'identity'
        divisa name:"divisa",column: "divisa"

    }
    /*Agregado para Portafolios*/
    String getDescLabel(){
        descripcion
    }
}
