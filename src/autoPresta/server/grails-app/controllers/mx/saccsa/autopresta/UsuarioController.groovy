package mx.saccsa.autopresta

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import mx.saccsa.common.Parametros
import mx.saccsa.restfull.CatalogoController
import mx.saccsa.security.*
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests

import java.text.SimpleDateFormat

class UsuarioController extends CatalogoController<Usuario>{
    def springSecurityService
    static allowedMethods = [save: "POST", create: "GET", update: "PUT", delete: "DELETE", index: "GET", "exportExcel":"POST","exportPDF":"POST",desbloquea: "POST"]

    static responseFormats = ['json']
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

    UsuarioController(){
        super(Usuario)
    }

    def index(){respond UsuarioRole.list().collect{
        [
                username: it.usuario.username.capitalize(),
                password: it.usuario.password,
                nombre: it.usuario.nombre,
                apellidoPaterno: it.usuario.apellidoPaterno,
                apellidoMaterno: it.usuario.apellidoMaterno,
                mail: it.usuario.mail,
                cargo: it.usuario.puesto,
                avatar: it.usuario.avatar,
                desde: it.usuario.desde,
                nuevo: it.usuario.nuevo,
                enabled: it.usuario.enabled,
                accountExpired: it.usuario.accountExpired,
                accountLocked: it.usuario.accountLocked,
                passwordExpired: it.usuario.passwordExpired,
                id:it.usuario.id,
                role: it.role.authority
        ]
    }}

    def create(){
        Usuario instance = new Usuario(avatar: "Avatar")
        respond instance
    }

    def edit(Long id){
        def u = Usuario.findById(id)
        respond u
    }

    @Transactional
    def save() {
        if(handleReadOnly()) {
            return
        }
        if(Usuario.countByUsername(request.JSON.username) >0){
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond mensaje: "El usuario con username [${request.JSON.username}] ya existe."
            return
        }
        Usuario instance = resource.newInstance()
        Usuario user = springSecurityService.getCurrentUser() as Usuario
        bindData instance, request.JSON

        instance.validate()

        if (instance.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond instance.errors, view:'create' // STATUS CODE 422
            return
        }
        def regexp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/
        def matcher = instance.password=~regexp
        if(!matcher){
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond mensaje: 'La contraseña debe ser de al menos 8 caracteres con al menos 1 letra mayuscula, 1 letra minuscula y 1 numero. No se permiten caracteres especiales.'
            return
        }

        instance.save(flush: true)
        /*================SAVE PERFIL=============*/
        def r = Role.findById(request.JSON.perfil)

        if(!UsuarioRole.create(instance,r,true)){
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond mensaje:"Error al asignar el perfil del usuario"
        }
        else
            respond mensaje:"Usuario y perfil creados correctamente"

        /*=====================================*/

    }
    @Transactional
    def savePerfil(){
        log.error params
        def r = Role.findById(params.long("perfil"))
        def u = Usuario.findById(params.long("usuario"))
        r?.lock()
        r?.refresh()

        u?.lock()
        u?.refresh()

        def ur =UsuarioRole.findAllByUsuario(u)
        ur*.delete(flush: true)
        if(!UsuarioRole.create(u,r,true)){
            transactionStatus.setRollbackOnly()
            render([mensaje:"Error al asignar el perfil del usuario"] as JSON)
        }
        else
            render([mensaje:"Perfil asignado correctamente"] as JSON)
    }
    @Transactional
    def cambiarPassword(){
        def noHistoria = Parametros.findByParametro("REST_NO_HISTORIA")?.valor ?: '5'
        String username = springSecurityService.principal.username
        String message=""
        if (!username) {
            transactionStatus.setRollbackOnly()
            message = 'No estas loggeado, no hay usuario en session'
            response.status = 422
            respond mensaje:message
            return
        }
        //"^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$"
        String password = params.password
        String newPassword = params.newpassword
        String newPassword2 = params.confirmarpassword
        def regexp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/
        def matcher = newPassword=~regexp
        if(!matcher){
            transactionStatus.setRollbackOnly()
            message = 'La contraseña debe ser de al menos 8 caracteres con al menos 1 letra mayuscula, 1 letra minuscula y 1 numero.'
            response.status = 422
            respond mensaje:message
            return
        }

        if (!password || !newPassword || !newPassword2 || newPassword != newPassword2) {
            transactionStatus.setRollbackOnly()
            message = 'Teclea tu password actual y un nuevo password valido.'
            response.status = 422
            respond mensaje:message
            return
        }

        Usuario user = Usuario.findByUsername(username)
        user?.lock()
        user?.refresh()
        log.error user
        if (!springSecurityService.passwordEncoder.isPasswordValid(user.password, password, null /*salt*/)) {
            transactionStatus.setRollbackOnly()
            message = 'El password actual es incorrecto'
            response.status = 422
            respond mensaje:message
            return
        }

        if (springSecurityService.passwordEncoder.isPasswordValid(user.password, newPassword, null /*salt*/)) {
            transactionStatus.setRollbackOnly()
            message = 'El password actual es igual al nuevo capturado'
            response.status = 422
            respond mensaje:message
            return
        }

        UsuarioHistorico.findAllByUsuario(user, [sort:"ultimaActualizacion", order:"desc"]).each{ uh ->
            if (springSecurityService.passwordEncoder.isPasswordValid(uh.password, newPassword, null /*salt*/)) {
                transactionStatus.setRollbackOnly()
                message = "El password ya se ha utilizado en las ultimas ${noHistoria?:3} actualizaciones de password."
                response.status = 422
                respond mensaje:message
                return
            }
        }
        def usuarioHistoria = new UsuarioHistorico()
        usuarioHistoria.password = user.password
        usuarioHistoria.ultimaActualizacion = new Date()
        usuarioHistoria.usuario = user
        user.password = newPassword
        user.nuevo = false
        usuarioHistoria.save(flush: true)
        user.save(flush:true) // if you have password constraints check them here
        List<UsuarioHistorico> listaUH = []
        UsuarioHistorico.findAllByUsuario(user, [sort:"ultimaActualizacion",order:"desc"]).eachWithIndex{ entry, i -> if(i >= Integer.parseInt(noHistoria)) listaUH.add entry }
        if(!listaUH.isEmpty()) listaUH*.delete(flush: true,failOnError:true)

        message="Password actualizado correctamente."
        respond mensaje:message

    }
    @Transactional
    def delete() {
        if(handleReadOnly()) {
            return
        }

        def instance = Usuario.load(params.id)
        if (instance == null) {
            notFound()
            return
        }

        try {
            instance.enabled = false;
            instance.accountLocked = true
            instance.accountExpired = true
            instance.save flush:true,failOnError:true
        }catch (Exception ex){
            ex.printStackTrace()
            response.status = 422
            respond mensaje:"Error al eliminar."
            return
        }

        respond mensaje:"Usuario eliminado."
    }
    @Transactional
    def update() {
        Boolean cambiaPassword =false
        if(handleReadOnly()) {
            return
        }
        def oldPass
        Usuario instance

        if(params.id == null)
            instance = resource.get(resource.newInstance(params))
        else
            instance = resource.get(params.id)

        if (instance == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }
        oldPass = instance.password
        instance.properties = request.JSON
        if (instance.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond instance.errors, view:'edit' // STATUS CODE 422
            return
        }
        log.error("OLD: "+oldPass + "NEW: "+instance.password)
        if(instance.password != oldPass){
            def regexp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/
            def matcher = instance.password=~regexp
            if(!matcher){
                transactionStatus.setRollbackOnly()
                response.status = 422
                respond mensaje: 'La contraseña debe ser de al menos 8 caracteres con al menos 1 letra mayuscula, 1 letra minuscula y 1 numero.'
                return
            }
            cambiaPassword= true
        }


        instance.save flush:true, failOError:true

        if(cambiaPassword) {
            def noHistoria = Parametros.findByParametro("REST_NO_HISTORIA")?.valor ?: '5'
            def usuarioHistoria = new UsuarioHistorico()
            usuarioHistoria.password = oldPass
            usuarioHistoria.ultimaActualizacion = new Date()
            usuarioHistoria.usuario = instance
            usuarioHistoria.save(flush: true)

            List<UsuarioHistorico> listaUH = []

            UsuarioHistorico.findAllByUsuario(instance, [sort: "ultimaActualizacion", order: "desc"]).eachWithIndex { entry, i -> if (i >= Integer.parseInt(noHistoria)) listaUH.add entry }

            if (!listaUH.isEmpty()) listaUH*.delete(flush: true, failOnError: true)
        }
        /*================SAVE PERFIL=============*/
        def r = Role.findById(request.JSON.perfil)

        if(!UsuarioRole.create(instance,r,true)){
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond mensaje:"Error al asignar el perfil del usuario"
        }
        else
            respond mensaje:"Usuario y perfil creados correctamente"

        /*=====================================*/
        respond mensaje:"Usuario Actualizado"
    }
    def autenticacion(){
        def username = request.JSON.j_username
        def pwd = request.JSON.j_password
        Usuario user = Usuario.findByUsername(username)
        def message
        if(!user){
            message = 'El usuario es incorrecto o no es autorizador'
            response.status = 422
            respond mensaje:message
            return
        }
        user?.lock()
        user?.refresh()
        log.error user
        if (!springSecurityService.passwordEncoder.isPasswordValid(user.password, pwd, null /*salt*/)) {
            transactionStatus.setRollbackOnly()
            message = 'El password actual es incorrecto'
            response.status = 422
            respond mensaje:message
            return
        }
        respond mensaje:"OK"
    }
    @Transactional
    def desbloquea(Long id){

        def username = Usuario.findById(id)?.username
        def usrAuth = AuthenticationToken.findByUsername(username)
        def message
        log.error "Desbloquear usuario: " + username

        if(!usrAuth){
            message = 'Token no encontrado'
            response.status = 422
            respond mensaje:message
            return
        }

        usrAuth.delete(flush:true)
        log.error "Unlock OK"
        respond mensaje:"OK"
    }
    @Transactional
    def actualizaPassword(){
        def noHistoria = Parametros.findByParametro("REST_NO_HISTORIA")?.valor ?: '5'
        String username = springSecurityService.principal.username
        String message=""
        if (!username) {
            transactionStatus.setRollbackOnly()
            message = 'No estas loggeado, no hay usuario en session'
            response.status = 422
            respond mensaje:message
            return
        }
        //"^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$"

        String newPassword = params.newpassword
        String newPassword2 = params.confirmarpassword
        def regexp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/
        def matcher = newPassword=~regexp
        if(!matcher){
            transactionStatus.setRollbackOnly()
            message = 'La contraseña debe ser de al menos 8 caracteres con al menos 1 letra mayuscula, 1 letra minuscula y 1 numero.'
            response.status = 422
            respond mensaje:message
            return
        }

        if ( !newPassword || !newPassword2 || newPassword != newPassword2) {
            transactionStatus.setRollbackOnly()
            message = 'Las contraseñas no coinciden.'
            response.status = 422
            respond mensaje:message
            return
        }

        Usuario user = Usuario.findByUsername(username)
        user?.lock()
        user?.refresh()


        if (springSecurityService.passwordEncoder.isPasswordValid(user.password, newPassword, null /*salt*/)) {
            transactionStatus.setRollbackOnly()
            message = 'El password actual es igual al nuevo capturado'
            response.status = 422
            respond mensaje:message
            return
        }

        UsuarioHistorico.findAllByUsuario(user, [sort:"ultimaActualizacion",order:"desc"]).each{uh ->
            if (springSecurityService.passwordEncoder.isPasswordValid(uh.password, newPassword, null /*salt*/)) {
                transactionStatus.setRollbackOnly()
                message = "El password ya se ha utilizado en las ultimas ${noHistoria?:3} actualizaciones de password."
                response.status = 422
                respond mensaje:message
                return
            }
        }
        def usuarioHistoria = new UsuarioHistorico()
        usuarioHistoria.password = user.password
        usuarioHistoria.ultimaActualizacion = new Date()
        usuarioHistoria.usuario = user
        user.password = newPassword
        user.nuevo = false
        usuarioHistoria.save(flush: true)
        user.save(flush:true) // if you have password constraints check them here
        List<UsuarioHistorico> listaUH = []
        UsuarioHistorico.findAllByUsuario(user, [sort:"ultimaActualizacion",order:"desc"]).eachWithIndex{ entry, i -> if(i >= Integer.parseInt(noHistoria)) listaUH.add entry }
        if(!listaUH.isEmpty()) listaUH*.delete(flush: true,failOnError:true)

        message="Password actualizado correctamente."
        respond mensaje:message

    }

    @Transactional
    def liberarSesion(){
        params
        request.JSON
        Usuario user = Usuario.findById(params.id as Long)
        AuthenticationToken authenticationToken = AuthenticationToken.findByUsername(user.username)
        if (authenticationToken==null){
            response.status = 400
            respond message:'El usuario no tiene una seseion activa'
            return
        }
        authenticationToken.delete(flush: true,failOnError:true)
        respond message: 'Session liberada para el usuario: ' + user.username
    }
}
