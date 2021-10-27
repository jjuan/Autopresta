package mx.saccsa.autopresta

import grails.rest.RestfulController
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class AgenciasController extends RestfulController<Agencias> {
    AgenciasController() {
        super(Agencias)
    }

    def index() {
        respond(Agencias.list().collect(
                {
                    [
                            id: it.id,
                            nombre: it.nombre,
                            sucursal: it.sucursal.descLabel,
                            region: it.region.descLabel,
                            portafolio: it.portafolio.descLabel,
                            clave: it.clave,
                    ]
                }
        ))
    }
}
