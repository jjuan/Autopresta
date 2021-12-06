package autopresta

import grails.gorm.transactions.Transactional
import mx.saccsa.autopresta.Portafolios
import mx.saccsa.security.Usuario
import mx.saccsa.security.UsuarioRole

import java.text.SimpleDateFormat

//import mx.saccsa.security.RoleDetalle
//import mx.saccsa.security.Usuario
//import mx.saccsa.security.UsuarioRole

class InitController {
    static responseFormats = ['json']
    def springSecurityService
    def sdf = new SimpleDateFormat('yyyy-MM-dd')

    @Transactional
    def getsession(){
        def currentUser = springSecurityService.getCurrentUser() as Usuario
        UsuarioRole role = UsuarioRole.findByUsuario(currentUser)
        session.setAttribute('user',currentUser)
        Portafolios portafolio = Portafolios.findByCvePortafolio(1)
        portafolio.fecha = sdf.parse(sdf.format(new Date()))
        portafolio.save(flush: true, failOnError: true)
//        session.setAttribute('portafolio',currentUser.portafolio)

        respond username: currentUser.username, role: role.role.authority, avatar: currentUser.avatar, usuario: currentUser.descLabel, puesto: currentUser.puesto.capitalize()
    }
}
