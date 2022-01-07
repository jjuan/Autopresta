package mx.saccsa.autopresta

class Banco {

    String descripcionCorta
    String descripcionLarga
    String direccion_clc
    String direccion_con
    String direccion_dis
    String pais

    static constraints = {
        descripcionCorta nullable: false, blank: false, size: 0..17
        descripcionLarga nullable: true, blank: true, size: 0..60
        direccion_clc nullable: true, blank: true, size: 0..8
        direccion_con nullable: true, blank: true, size: 0..8
        direccion_dis nullable: true, blank: true, size: 0..8
        pais nullable: false, blank: false, size: 0..20
    }

    static mapping = {
        //id generator:'sequence', params:[sequence:'BANCO_SEQ']
        table "BANCO"
        id generator: 'identity'
        version false
    }
    static transients = ['descLabel']

    String getDescLabel() { descripcionCorta }
}
