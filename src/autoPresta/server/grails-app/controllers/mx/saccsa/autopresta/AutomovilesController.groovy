package mx.saccsa.autopresta

import mx.saccsa.restfull.CatalogoController
import grails.gorm.transactions.ReadOnly
@ReadOnly
class AutomovilesController extends CatalogoController<Automoviles> {
    AutomovilesController() {
        super(Automoviles)
    }

    def cargaMasiva() {
        request.JSON
        params
        def automoviles = ContactoAutotal.list()

        for (auto in automoviles) {
            log.error ''+ auto

            Marcas marcaAuto = Marcas.findByNombre(auto.marca)
            if (marcaAuto == null) {
                Marcas altaMarca = new Marcas()
                altaMarca.nombre = auto.marca
                altaMarca.slug = auto.marca
                altaMarca.save(flush: true, failOnError: true)
                marcaAuto = Marcas.findByNombre(auto.marca)
            }

            Modelos modeloAuto = Modelos.findByMarcaAndNombre(marcaAuto, auto.modelo)
            if (modeloAuto == null) {
                Modelos altaModelo = new Modelos()
                altaModelo.marca = marcaAuto
                altaModelo.nombre = auto.modelo
                altaModelo.slug = auto.modelo
                altaModelo.save(flush: true, failOnError: true)
                modeloAuto = Modelos.findByMarcaAndNombre(marcaAuto, auto.modelo)
            }

            Automoviles automovil = Automoviles.findByMarcaAndModeloAndAnio(marcaAuto, modeloAuto, auto.year)
            if (automovil == null) {
                Automoviles altaModelo = new Automoviles()
                altaModelo.marca = marcaAuto
                altaModelo.modelo = modeloAuto
                altaModelo.anio = auto.year
                altaModelo.save(flush: true, failOnError: true)
                automovil = Automoviles.findByMarcaAndModeloAndAnio(marcaAuto, modeloAuto, auto.year)
            }
            log.error 'marca: ' + automovil.marca.nombre + ' modelo: ' + automovil.modelo.nombre + ' año: ' + automovil.anio.toString()
        }

        respond message: 'registros insertados'
    }

    def index() {
        respond(Automoviles.list().collect({
            [
                    id: it.id,
                    marca: it.marca.nombre,
                    modelo: it.modelo.nombre,
                    anio: it.anio
            ]
        }))
    }

}
