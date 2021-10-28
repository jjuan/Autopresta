package mx.saccsa.autopresta

import grails.rest.RestfulController

class ComboController {

    def comboAutoPresta(String id){
        String groupId = 'mx.saccsa.autopresta'
//        log.error groupId+"."+id
        Class c = Class.forName(groupId+"."+id.capitalize())
        def data = c.list().collect{
            [
                    id:it?.id,
                    descripcion: it?.descLabel
            ]
        }
        respond data
    }

    def comboSecurity(String id){
        String groupId = 'mx.saccsa.security'
//        log.error groupId+"."+id
        Class c = Class.forName(groupId+"."+id)
        def data = c.list().collect{
            [
                    id:it.getId(),
                    descripcion: it.getDescLabel()
            ]
        }
        respond data
    }
    def comboMercados(){
        def data = Mercados.list().collect{
            [
                    id:it.cveMercado,
                    descripcion: it.getDescLabel()
            ]
        }
        respond data
    }

    def comboPortafolios(){
        def data = Portafolios.list().collect{
            [
                    id:it.cvePortafolio,
                    descripcion: it.getDescLabel()
            ]
        }
        respond data
    }

    def comboDivisas(){
        def data = Divisas.list().collect{
            [
                    id:it.clave,
                    descripcion: it.getDescLabel()
            ]
        }
        respond data
    }

    def comboMarcas(){
        def data = Marcas.list().collect{
            [
                    id:it.id,
                    descripcion: it.getDescLabel()
            ]
        }
        respond data
    }

    def comboFactura(String id){
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

    def comboColonias(String id){
        respond(Cp.findAllByCodigoPostal(id).collect({
            [
                    id: it.asentamiento,
                    descripcion: it.asentamiento
            ]
        }))
    }

    def comboModelos(Long id){
        respond(Modelos.findAllByMarca(Marcas.findById(id)).collect({
            [
                    id: it.id,
                    descripcion: it.nombre
            ]
        }))
    }

}
