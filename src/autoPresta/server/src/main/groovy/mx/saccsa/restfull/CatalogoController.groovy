package mx.saccsa.restfull

import grails.artefact.Artefact
import grails.gorm.transactions.Transactional
import grails.rest.RestfulController
import grails.web.http.HttpHeaders
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException

import java.lang.reflect.ParameterizedType
import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException

import static org.springframework.http.HttpStatus.*

@Artefact("Controller")
@Transactional(readOnly = true)
class CatalogoController<T> extends RestfulController<T>{
    static responseFormats = ['json']
    static allowedMethods = [save: "POST", create: "GET", update:["PUT", "POST"], delete: "DELETE", index: "GET"]

    private Class<T> type=null

    CatalogoController(Class<T> catalogo){
        super(catalogo)
    }

    @Override
    def index(Integer max) {
        if(max){
            params.max = max
        }
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
    }
    @Override
    def edit(){
        def id
        if(params.id == null)
            id = resource.newInstance(params)
        else
            id = params.id
        respond queryForResource(id)
    }
    @Override
    def show() {
        def id
        if(params.id == null)
            id = resource.newInstance(params)
        else
            id = params.id
        respond queryForResource(id)
    }
    /**
     * Updates a resource for the given id
     * @param id
     */
    @Override
    @Transactional
    def update() {

        if(handleReadOnly()) {
            return
        }
        def id
        if(params.id == null)
            id = resource.newInstance(params)
        else
            id = params.id

        T instance =  queryForResource(id)

        if (instance == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        instance.properties = request.JSON
        if (instance.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond instance.errors, view:'edit' // STATUS CODE 422
            return
        }

        instance.save flush:true, failOError:true
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: "${resourceClassName}.label".toString(), default: resourceClassName), instance.id])
                redirect instance
            }
            '*'{
                response.addHeader(HttpHeaders.LOCATION,
                        grailsLinkGenerator.link( resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                respond instance, [status: OK]
            }
        }
    }

    @Override
    @Transactional
    def save() {
        if(handleReadOnly()) {
            return
        }
        def instance = resource.newInstance()
        bindData instance, request.JSON

        instance.validate()

        if (instance.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond instance.errors, view:'create' // STATUS CODE 422
            return
        }

        try {
            instance.save insert:true, flush: true, failOnError: true
        }catch(DuplicateKeyException duplicateExeption){
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond errors:[[message: "Registro duplicado"]]
            return
        }catch(SQLException sql){
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond errors:[[message: "Error en base de datos: "+sql.message]]
            return
        }catch(Exception ex){
            transactionStatus.setRollbackOnly()
            response.status = 422
            respond errors:[[message:"Excepcion generada al momento de guardar el registor. "+ex.message]]
            return
        }


        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: "${resourceName}.label".toString(), default: resourceClassName), instance.id])
                redirect instance
            }
            '*' {
                response.addHeader(HttpHeaders.LOCATION,
                        grailsLinkGenerator.link( resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                respond instance, [status: CREATED, view: 'show']
            }
        }
    }


    @Override
    @Transactional
    def delete() {
        if(handleReadOnly()) {
            return
        }
        def instance
        if(params.id==null)
            instance =  resource.get(resource.newInstance(params))
        else
            instance =  resource.get(params.id)
        if (instance == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        try {
            instance.delete flush: true
        }catch (DataIntegrityViolationException dataIntegrityViolationException){
            response.status = 422
            respond errors:[[message:"No puede eliminarse el registro. Existen referencias a este registro."]]
            return
        }catch (SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException){
            response.status = 422
            respond errors:[[mensaje:"No puede eliminarse el registro. Existen referencias a este registro."]]
            return
        }catch(SQLException sql){
            response.status = 422
            respond errors:[[mensaje:"Error en base de datos al eliminar el registro."+sql.message]]
            return
        }catch(Exception ex){
            response.status = 422
            respond errors:[[mensaje:"Excepcion al eliminar el registro. "+ex.message]]
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [classMessageArg, instance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT } // NO CONTENT STATUS CODE
        }
    }
    private Class<T> getType() {
        if (type == null) {
            Class clazz = getClass()
            while (!(clazz.getGenericSuperclass() instanceof ParameterizedType))
                clazz = clazz.getSuperclass()
            type = (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        }

        return type;
    }
}
