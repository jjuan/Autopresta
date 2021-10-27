package mx.saccsa.security

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.saccsa.autopresta.Portafolios

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class Usuario implements Serializable {

    private static final long serialVersionUID = 1

    String username
    String password

    String nombre
    String apellidoPaterno
    String apellidoMaterno
    String mail
    String puesto
    String avatar = "Avatar"
    Date desde
    Boolean nuevo = true

//    Portafolios portafolio

    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    Set<Role> getAuthorities() {
        (UsuarioRole.findAllByUsuario(this) as List<UsuarioRole>)*.role as Set<Role>
    }

    static constraints = {
        password blank: false, password: true
        username blank: false, unique: true
        nombre size: 1..100
        apellidoPaterno size: 1..100, nullable: true, blank: true
        apellidoMaterno size: 1..100, nullable: true, blank: true
        mail size: 1..200, nullable: true, blank: true
        puesto nullable: false, blank: false
        avatar size:0..200, nullable: true, blank:true
        desde nullable: true, blank:true
//        portafolio nullable: false, blank: false
        nuevo nullable: true, blank:true
    }

    static mapping = {
        table "USUARIO"
        version false
        id generator : "identity"
        password column: '`password`'
    }

    static transients = ['descLabel']
    String getDescLabel() { nombre + ' ' + apellidoPaterno + ' ' + apellidoMaterno}
}
