package mx.saccsa.common

class Parametros {

    String parametro
    String valor
    static constraints = {
        parametro size: 0..100
        valor size: 0..300
    }
    static mapping = {
        table "PARAMETROS"
        version false
        id generator:"assigned", name:'parametro', column:"parametro"
        parametro name:"parametro", column: "parametro"
        valor name:"valor", column: "valor"
    }
    static getValorByParametro(String parametro){
        Parametros.findByParametro(parametro)?.valor
    }
}
