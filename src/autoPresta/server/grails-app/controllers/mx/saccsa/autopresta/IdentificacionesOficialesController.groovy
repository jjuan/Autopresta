package mx.saccsa.autopresta

import grails.validation.ValidationException
import mx.saccsa.restfull.CatalogoController

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class IdentificacionesOficialesController extends CatalogoController<IdentificacionesOficiales>{
    IdentificacionesOficialesController() {
        super(IdentificacionesOficiales)
    }

}
