package mx.saccsa.autopresta

class Mercados {

    String	cveMercado
    String	descripcion
    Date	fecha
    static constraints = {
        cveMercado	nullable: true, size: 0..2
        descripcion	nullable: true, size: 0..30
        fecha	nullable:true
    }
    static mapping = {
        table	name: 'Mercados'
        id	column: 'cveMercado', name: 'cveMercado', generator: 'assigned'
        cveMercado	name: 'cveMercado', column: 'cveMercado'
        descripcion	name: 'descripcion', column: 'descripcion'
        fecha	name:'fecha', column:'fecha'
    }

    static transients = ['descLabel']
    String getDescLabel(){
        descripcion
    }
}
