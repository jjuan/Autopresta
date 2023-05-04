package mx.saccsa.autopresta

import grails.gorm.transactions.Transactional
import grails.rest.RestfulController
import mx.saccsa.security.Usuario

import java.text.SimpleDateFormat

class SucursalesController extends RestfulController<Sucursales> {
    SucursalesController() {
        super(Sucursales)
    }
    def springSecurityService
    def sdf = new SimpleDateFormat('yyyy-MM-dd')

    def index() {
        respond(Sucursales.list().collect(
                {
                    [
                            id          : it?.id,
                            descripcion : it?.descripcion,
                            region      : it?.region?.descLabel,
                            ciudad      : it?.ciudad,
                            direccion   : it?.direccion,
                            colonia     : it?.colonia,
                            codigoPostal: it?.codigoPostal,
                            telefono    : it?.telefono,
                            fecha       : it?.fecha,
                    ]
                }
        ))
    }

    def cargarSucursal() {
        Usuario user = springSecurityService.getCurrentUser()
        respond sucursal: user.sucursal.id, fecha: user.sucursal.fecha
    }

    def cargarFolio() {
        Usuario user = springSecurityService.getCurrentUser()
        Regiones reg = user.sucursal.region
        String tipoFolio = Combo.findByComboAndHabilitadoAndClave('TipoContrato', true, reg.clave).descripcion


        Folios folio = Folios.findByCveTipo(getClave(reg.clave))
        def folioRecuperado = getFolioRecuperado(getClave(reg.clave))
        String f = folio != null ? (folio.folio + 1).toString() : '1'
        String folioContrato = folioRecuperado > 0 ? folioRecuperado : f
//        respond folio: contratoFolio(folioContrato, clave)

        respond sucursal: user.sucursal.id, fecha: user.sucursal.fecha, tipoContrato: reg.clave, numeroContrato: contratoFolio(folioContrato, tipoFolio)
    }

    @Transactional
    def actualizaFecha() {
        Sucursales sc = Sucursales.findById(request.JSON.id as Long)
        sc.fecha = request.JSON.fecha ? sdf.parse(request.JSON.fecha) : null
        sc.save(flush: true, failOnError: true)
        respond sc
    }

    @Transactional
    def actualizaSucursal() {
        Sucursales sc = Sucursales.findById(request.JSON.sucursal as Long)
        Usuario user = springSecurityService.getCurrentUser()
        user.sucursal = sc
        user.save(flush: true, failOnError: true)
        respond sucursal: user.sucursal.id, fecha: user.sucursal.fecha
    }

    def getClave(String tipoFolio) {
        String cveTipo = ''
        switch (tipoFolio) {
            case 'CDMX':
                cveTipo = 'CONTRATO'
                break
            case 'GDL':
                cveTipo = 'CONTRATOGUADALAJARA'
                break
            case 'MTY':
                cveTipo = 'CONTRATOMONTERREY'
                break
            case 'P':
                cveTipo = 'CONTRATOPRUEBAS'
                break
        }
        return cveTipo
    }


    def getFolioRecuperado(String cveTipo) {
        def validar = FoliosRecuperados.findAllByCveTipo(cveTipo)
        if (validar.size() > 0) {
            def folioRecuperado = FoliosRecuperados.executeQuery('Select min(folio) from FoliosRecuperados where cveTipo= :cveTipo', [cveTipo: cveTipo])
            return new Long(folioRecuperado[0] as String)
        } else {
            return 0
        }
    }

    String contratoFolio(String folio, String tipoFolio) {
        switch (tipoFolio) {
            case 'CDMX':
                while (folio.length() < 5) {
                    folio = '0' + folio
                }
                break
            case 'GDL':
                while (folio.length() < 5) {
                    folio = '0' + folio
                }
                folio = 'GDL' + folio
                break
            case 'MTY':
                while (folio.length() < 5) {
                    folio = '0' + folio
                }
                folio = 'MTY' + folio
                break
            case 'P':
                folio = folio + 'P'
                while (folio.length() < 6) {
                    folio = '0' + folio
                }
                break
        }
        return folio
    }

}
