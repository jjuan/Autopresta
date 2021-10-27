package mx.saccsa.autopresta

class Direccion {
    Contrato contrato
    Boolean dirTrabajo
    Boolean dirAdicional
    String direccionPrincipal
    String exterior
    String interior
    Long cp
    String colonia
    String municipio
    String entidad
    Boolean principal

    static constraints = {
        contrato nullable: false, blank: false
        dirTrabajo nullable: true, blank: true
        dirAdicional nullable: true, blank: true
        direccionPrincipal nullable: true, blank: true
        exterior nullable: true, blank: true
        interior nullable: true, blank: true
        cp nullable: true, blank: true
        colonia nullable: true, blank: true
        municipio nullable: true, blank: true
        entidad nullable: true, blank: true
    }

    static mapping = {
        table('Direccion')
        version(false)
        id generator: 'identity'
        contrato name: 'contrato', column: 'contrato'
        dirTrabajo name: 'dirTrabajo', column: 'dirTrabajo'
        dirAdicional name: 'dirAdicional', column: 'dirAdicional'
        direccionPrincipal name: 'direccionPrincipal', column: 'direccionPrincipal'
        exterior name: 'exterior', column: 'exterior'
        interior name: 'interior', column: 'interior'
        cp name: 'cp', column: 'cp'
        colonia name: 'colonia', column: 'colonia'
        municipio name: 'municipio', column: 'municipio'
        entidad name: 'entidad', column: 'entidad'
    }
    }
