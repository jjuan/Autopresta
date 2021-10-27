package mx.saccsa.autopresta

class Estados {
    String descripcion

    static mapping = {
        table ("Estados")
        version(false)
        id generator: 'identity'
    }

    static transients = ['descLabel']
    String getDescLabel() { descripcion }
}
