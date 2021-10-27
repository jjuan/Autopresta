package mx.saccsa.autopresta

class Regiones {
    String clave
    String descripcion
    BigDecimal variacion=0.0

    transient static def headers=['Clave','Descripcion']
    transient static def withProperties=['clave','descripcion']

    static constraints = {
        clave nullable: false,blank:false,size:0..4
        descripcion nullable: false,blank:false,size:0..30
        variacion nullable:true
    }
    static mapping = {
        table "Regiones"
        //id(generator:'sequence',params:['sequence':'REGION_SEQ'])
        id generator: 'identity'
        clave name:'clave', column:'clave'
        descripcion name:'descripcion', column:'descripcion'
        variacion name:'variacion', column:'variacion'
    }
    static transients = ['descLabel']
    String getDescLabel(){
        descripcion
    }
}
