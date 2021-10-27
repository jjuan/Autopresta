package mx.saccsa.autopresta

class Cp {
    String codigoPostal
    String asentamiento
    String ciudad
    String municipio
    String estado
    static constraints = {
        codigoPostal nullable: false, blank: false
        asentamiento nullable: true, blank: true
        ciudad nullable: true, blank: true
        municipio nullable: true, blank: true
        estado nullable: true, blank: true
    }
    static mapping = {
        table ("Cp")
        version(false)
        id generator: 'identity'
    }

    static transients = ['descLabel']
    String getDescLabel() { 'CP: ' + codigoPostal + ', ' + ciudad + ', ' + municipio + ', ' + asentamiento}
}
