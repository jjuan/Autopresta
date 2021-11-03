package mx.saccsa.autopresta

class RazonesSociales {
    String razonSocial
    String rfc
    String telefonoFijo
    String telefonoCelular
    String telefonoOficina
    String calleDireccionFiscal
    String numeroExterior
    String numeroInterior
    String codigoPostal
    String colonia
    String entidad
    String municipio

    static constraints = {
        razonSocial nullable: true, blank: true
        rfc nullable: true, blank: true
        telefonoFijo nullable: true, blank: true
        telefonoCelular nullable: true, blank: true
        telefonoOficina nullable: true, blank: true
        calleDireccionFiscal nullable: true, blank: true
        numeroExterior nullable: true, blank: true
        numeroInterior nullable: true, blank: true
        codigoPostal nullable: true, blank: true
        colonia nullable: true, blank: true
        entidad nullable: true, blank: true
        municipio nullable: true, blank: true
    }

    static mapping = {
        table('RazonesSociales')
        version(false)
        razonSocial name: 'razonSocial', column: 'razonSocial'
        rfc name: 'rfc', column: 'rfc'
        telefonoFijo name: 'telefonoFijo', column: 'telefonoFijo'
        telefonoCelular name: 'telefonoCelular', column: 'telefonoCelular'
        telefonoOficina name: 'telefonoOficina', column: 'telefonoOficina'
        calleDireccionFiscal name: 'calleDireccionFiscal', column: 'calleDireccionFiscal'
        numeroExterior name: 'numeroExterior', column: 'numeroExterior'
        numeroInterior name: 'numeroInterior', column: 'numeroInterior'
        codigoPostal name: 'codigoPostal', column: 'codigoPostal'
        colonia name: 'colonia', column: 'colonia'
        entidad name: 'entidad', column: 'entidad'
        municipio name: 'municipio', column: 'municipio'
    }

    static transients = ['descLabel']
    String getDescLabel() {
        razonSocial
    }
}
