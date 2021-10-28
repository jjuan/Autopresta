package mx.saccsa.autopresta

class Contrato {
    //datos del cliente
    C_RegimenFiscal regimenFiscal
    String nombres
    String primerApellido
    String segundoApellido
    String genero
    String rfc
    Long edad
    String fechaNacimiento
    String curp
    String claveElector
    String telefonoFijo
    String telefonoCelular
    String telefonoOficina
    String correoElectronico

    //datos del automovil
    String anio
    Marcas marca
    Modelos modelo
    String versionAuto

    String color
    String placas
    String numeroDeMotor
    String numeroDeFactura
    Date fechaDeFactura
    String emisoraDeFactura
    BigDecimal valorDeVenta
    BigDecimal valorDeCompra
    BigDecimal montoMaximoAutorizado
    String numeroVin

    //servicios
    Gps gps1
    Proveedores proveedor1
    Gps gps2
    Proveedores proveedor2
    Gps gps3
    Proveedores proveedor3

    //datosdel prestamo
    BigDecimal montoRequerido
    BigDecimal costoMensualInteres
    BigDecimal costoMensualMonitoreo
    BigDecimal costoMensualGPS
    BigDecimal totalAutoPresta
    BigDecimal iva
    BigDecimal costoMensualTotal
    TipoContrato tipoContrato

    String estatus
    String referencia
    String clabe

    static constraints = {
        regimenFiscal nullable: true, blank: true

        nombres nullable: true, blank: true
        primerApellido nullable: true, blank: true
        segundoApellido nullable: true, blank: true
        genero nullable: true, blank: true
        edad nullable: true, blank: true
        rfc nullable: true, blank: true
        fechaNacimiento nullable: true, blank: true
        curp nullable: true, blank: true
        claveElector nullable: true, blank: true
        telefonoFijo nullable: true, blank: true
        telefonoCelular nullable: true, blank: true
        telefonoOficina nullable: true, blank: true
        correoElectronico nullable: true, blank: true

        anio nullable: true, blank: true
        marca nullable: true, blank: true
        modelo nullable: true, blank: true
        versionAuto nullable: true, blank: true

        color nullable: true, blank: true
        placas nullable: true, blank: true
        numeroDeMotor nullable: true, blank: true
        numeroDeFactura nullable: true, blank: true
        fechaDeFactura nullable: true, blank: true
        emisoraDeFactura nullable: true, blank: true
        valorDeVenta nullable: true, blank: true
        valorDeCompra nullable: true, blank: true
        montoMaximoAutorizado nullable: true, blank: true
        numeroVin nullable: true, blank: true
        gps1 nullable: true, blank: true
        proveedor1 nullable: true, blank: true
        gps2 nullable: true, blank: true
        proveedor2 nullable: true, blank: true
        gps3 nullable: true, blank: true
        proveedor3 nullable: true, blank: true
        montoRequerido nullable: true, blank: true
        costoMensualInteres nullable: true, blank: true
        costoMensualMonitoreo nullable: true, blank: true
        costoMensualGPS nullable: true, blank: true
        totalAutoPresta nullable: true, blank: true
        iva nullable: true, blank: true
        costoMensualTotal nullable: true, blank: true
        tipoContrato nullable: true, blank: true
        estatus nullable: true, blank: true
        referencia nullable: true, blank: true
        clabe nullable: true, blank: true
    }

    static mapping = {
        table('Contrato')
        version(false)
        id generator: 'identity'
        regimenFiscal name: 'regimenFiscal', column: 'regimenFiscal'

        nombres name: 'nombres', column: 'nombres'
        primerApellido name: 'primerApellido', column: 'primerApellido'
        segundoApellido name: 'segundoApellido', column: 'segundoApellido'
        genero name: 'genero', column: 'genero'
        rfc name: 'rfc', column: 'rfc'
        edad name: 'edad', column: 'edad'
        fechaNacimiento name: 'fechaNacimiento', column: 'fechaNacimiento'
        curp name: 'curp', column: 'curp'
        claveElector name: 'claveElector', column: 'claveElector'
        telefonoFijo name: 'telefonoFijo', column: 'telefonoFijo'
        telefonoCelular name: 'telefonoCelular', column: 'telefonoCelular'
        telefonoOficina name: 'telefonoOficina', column: 'telefonoOficina'
        correoElectronico name: 'correoElectronico', column: 'correoElectronico'

        anio column: 'anio', name: 'anio'
        marca column: 'marca', name: 'marca'
        modelo column: 'modelo', name: 'modelo'
        versionAuto column: 'versionAuto', name: 'versionAuto'

        color name: 'color', column: 'color'
        placas name: 'placas', column: 'placas'
        numeroDeMotor name: 'numeroDeMotor', column: 'numeroDeMotor'
        numeroDeFactura name: 'numeroDeFactura', column: 'numeroDeFactura'
        fechaDeFactura name: 'fechaDeFactura', column: 'fechaDeFactura'
        emisoraDeFactura name: 'emisoraDeFactura', column: 'emisoraDeFactura'
        valorDeVenta name: 'valorDeVenta', column: 'valorDeVenta'
        valorDeCompra name: 'valorDeCompra', column: 'valorDeCompra'
        montoMaximoAutorizado name: 'montoMaximoAutorizado', column: 'montoMaximoAutorizado'
        numeroVin name: 'numeroVin', column: 'numeroVin'
        gps1 name: 'gps1', column: 'gps1'
        proveedor1 name: 'proveedor1', column: 'proveedor1'
        gps2 name: 'gps2', column: 'gps2'
        proveedor2 name: 'proveedor2', column: 'proveedor2'
        gps3 name: 'gps3', column: 'gps3'
        proveedor3 name: 'proveedor3', column: 'proveedor3'
        montoRequerido name: 'montoRequerido', column: 'montoRequerido'
        costoMensualInteres name: 'costoMensualInteres', column: 'costoMensualInteres'
        costoMensualMonitoreo name: 'costoMensualMonitoreo', column: 'costoMensualMonitoreo'
        costoMensualGPS name: 'costoMensualGPS', column: 'costoMensualGPS'
        totalAutoPresta name: 'totalAutoPresta', column: 'totalAutoPresta'
        iva name: 'iva', column: 'iva'
        costoMensualTotal name: 'costoMensualTotal', column: 'costoMensualTotal'
        estatus name: 'estatus', column: 'estatus'
        tipoContrato name: 'tipoContrato', column: 'tipoContrato'
        referencia name: 'referencia', column: 'referencia'
        clabe name: 'clabe', column: 'clabe'
    }
}
