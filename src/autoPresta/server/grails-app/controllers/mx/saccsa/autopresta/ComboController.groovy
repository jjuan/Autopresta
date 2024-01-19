package mx.saccsa.autopresta

import grails.rest.RestfulController
import mx.saccsa.common.Parametros

class ComboController {

    def combos(String tipo) {
        respond Combo.findAllByComboAndHabilitado(tipo, true).collect({
            [
                    id         : it.clave,
                    descripcion: it.descripcion
            ]
        })
    }

    def comboAutoPresta(String id) {
        String groupId = 'mx.saccsa.autopresta'
//        log.error groupId+"."+id
        Class c = Class.forName(groupId + "." + id.capitalize())
        def data = c.list().collect {
            [
                    id         : it?.id,
                    descripcion: it?.descLabel
            ]
        }
        respond data
    }

    def comboController(String id) {
        String groupId = 'mx.saccsa.autopresta'
//        log.error groupId+"."+id
        Class c = Class.forName(groupId + "." + id.capitalize())
        def data = c.list().collect {
            [
                    id         : it?.id,
                    descripcion: it?.descLabel
            ]
        }
        respond data
    }

    def comboCuenta(String id, String banco) {
        def cuentas
        Banco bancoId = Banco.findById(banco as long)
        if (id != '0' && banco != '0') {
            Portafolios razonSocial = Portafolios.findById(id as Long);
            cuentas = CuentasBancarias.findAllByRazonSocialAndBanco(razonSocial, bancoId)
        } else if (id != '0' && banco == '0') {
            Portafolios razonSocial = Portafolios.findById(id as Long);
            cuentas = CuentasBancarias.findAllByRazonSocial(razonSocial)
        } else if (id == '0' && banco != '0') {
            cuentas = CuentasBancarias.findAllByBanco(bancoId)
        }
        respond cuentas.collect {
            [
                    id         : it.id,
                    descripcion: it.alias,
                    cuenta     : it.cuenta
            ]
        }
    }

    def comboPortafolios(String id) {
        def data = Portafolios.list().collect {
            [
                    id         : it?.cvePortafolio,
                    descripcion: it?.descLabel
            ]
        }
        respond data
    }

    def comboSecurity(String id) {
        String groupId = 'mx.saccsa.security'
//        log.error groupId+"."+id
        Class c = Class.forName(groupId + "." + id)
        def data = c.list().collect {
            [
                    id         : it.getId(),
                    descripcion: it.getDescLabel()
            ]
        }
        respond data
    }

    def comboMercados() {
        def data = Mercados.list().collect {
            [
                    id         : it.cveMercado,
                    descripcion: it.getDescLabel()
            ]
        }
        respond data
    }

//    def comboPortafolios(){
//        def data = Portafolios.list().collect{
//            [
//                    id:it.cvePortafolio,
//                    descripcion: it.getDescLabel()
//            ]
//        }
//        respond data
//    }

    def comboDivisas() {
        def data = Divisas.list().collect {
            [
                    id         : it.clave,
                    descripcion: it.getDescLabel()
            ]
        }
        respond data
    }

    def comboMarcas() {
        def data = Marcas.list().collect {
            [
                    id         : it.id,
                    descripcion: it.getDescLabel()
            ]
        }

        data = data.sort({ it.descripcion })
        respond data
    }

    def comboFactura(String id) {
        String groupId = 'mx.saccsa.autopresta'
        Class c = Class.forName(groupId + "." + id.capitalize())
        def data = c.list().collect {
            [
                    id         : it.clave,
                    descripcion: it.descLabel
            ]
        }
        respond data
    }

    def comboCp(String id) {
        Estados estado = Estados.findById(id as long)
        def lista = Cp.findAllByEstado(estado.descripcion as String).collect({
            [
                    id          : it.codigoPostal,
                    descripcion : it.descLabel,
                    estado      : it.estado,
                    municipio   : it.municipio,
                    ciudad      : it.ciudad,
                    asentamiento: it.asentamiento
            ]
        })
        respond(lista)
    }

    def comboColonias(String id) {
        respond(Cp.findAllByCodigoPostal(id).collect({
            [
                    id         : it.asentamiento,
                    descripcion: it.asentamiento
            ]
        }))
    }

    def comboModelos(Long id) {
        def data = Modelos.findAllByMarca(Marcas.findById(id)).collect({
            [
                    id         : it.id,
                    descripcion: it.nombre
            ]
        })
        data = data.sort({ it.descripcion })
        respond(data)
    }

    def comboAutos(Long id) {
        def data = Automoviles.findAllByModelo(Modelos.findById(id)).collect({
            [
                    id         : it.id,
                    descripcion: it.anio
            ]
        })
        data = data.sort({ it.descripcion })
        respond(data)
    }

    def comboDocumentos() {
        def data = IdentificacionesOficiales.list()
        respond(data)
    }

    def maxAutorizado(){
        def max = Parametros.getValorByParametro('MontoMaximoAutorizado')
        respond max: max
    }

    def comboDir(Long cve) {
        respond Combo.findAllByComboAndOrden('TipoDireccion', cve).collect({
            [
                    id         : it.clave,
                    descripcion: it.descripcion
            ]
        })
    }
}
