package mx.saccsa.security

class UsuarioHistorico {

    Usuario usuario
    String password
    Date ultimaActualizacion

    static constraints = {
    }
    static mapping = {
        table"USUARIOHISTORICO"
        version false
        id generator: "identity"
        password column: '`password`'
    }
}
