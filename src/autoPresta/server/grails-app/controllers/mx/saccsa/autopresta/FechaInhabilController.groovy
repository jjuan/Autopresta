package mx.saccsa.autopresta

import grails.validation.ValidationException
import mx.saccsa.restfull.CatalogoController

import java.text.SimpleDateFormat

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class FechaInhabilController extends CatalogoController<FechaInhabil> {

    FechaInhabilController() { super(FechaInhabil) }
    def sdf = new SimpleDateFormat('yyyy-MM-dd')

    def index() {
        respond FechaInhabil.list().collect({
            [
                    id         : it.id,
                    fecha      : it.fecha,
                    descripcion: it.descripcion,
                    divisa     : it.divisa?.descripcion
            ]
        })
    }

    def save() {
        params
        request.JSON
        FechaInhabil fechaInhabil = resource.newInstance()
//        bindData fechaInhabil, request.JSON
        def fecha = (!request.JSON?.fecha)?null:(sdf.parse(request.JSON?.fecha))
        fechaInhabil.fecha = fecha
        fechaInhabil.descripcion = request.JSON.descripcion
        fechaInhabil.divisa = Divisas.findById(request.JSON.divisa as Long)
        fechaInhabil.validate()
        fechaInhabil.save(flush: true, failOnError: true)
        respond(fechaInhabil)
    }

    def update(Long id) {
        FechaInhabil fechaInhabil = FechaInhabil.findById(id)
        def fecha = (!request.JSON?.fecha)?null:(sdf.parse(request.JSON?.fecha))
        fechaInhabil.fecha = fecha
        fechaInhabil.descripcion = request.JSON.descripcion
        fechaInhabil.divisa = Divisas.findById(request.JSON.divisa as Long)
        fechaInhabil.validate()
        fechaInhabil.save(flush: true, failOnError: true)
        respond(fechaInhabil)
    }
}
