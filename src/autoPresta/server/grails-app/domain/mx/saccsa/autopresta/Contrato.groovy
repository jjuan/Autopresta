package mx.saccsa.autopresta

class Contrato {
    //datos del cliente
    C_RegimenFiscal regimenFiscal
    Date fechaContrato
    String nombres
    String primerApellido
    String segundoApellido
    String genero
    String rfc
    Long edad
    String fechaNacimiento
    String curp
    String claveElector
    String documentoOficial
    String telefonoFijo
    String telefonoCelular
    String telefonoOficina
    String correoElectronico

    String nombresCoacreditado
    String primerApellidoCoacreditado
    String segundoApellidoCoacreditado
    String generoCoacreditado
    String rfcCoacreditado
    Long edadCoacreditado
    String fechaNacimientoCoacreditado
    String curpCoacreditado
    IdentificacionesOficiales documentoOficialCoacreditado
    String claveElectorCoacreditado
    String telefonoFijoCoacreditado
    String telefonoCelularCoacreditado
    String telefonoOficinaCoacreditado
    String correoElectronicoCoacreditado

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
//    Proveedores proveedor1
    Gps gps2
//    Proveedores proveedor2
    Gps gps3
//    Proveedores proveedor3

    //datosdel prestamo
    BigDecimal montoRequerido
    BigDecimal costoMensualInteres
    BigDecimal costoMensualMonitoreo
    BigDecimal costoMensualGPS
    BigDecimal totalAutoPresta
    BigDecimal iva
    BigDecimal costoMensualTotal
    TipoContrato tipoContrato

    String estatus = 'R'
    String referencia
    String clabe
    RazonesSociales razonesSociales

    CalificacionCliente calificacionCliente
    String numeroContrato
    Boolean contratoPrueba = false
    BigDecimal montoTransferencia
    String detalleDescuentos
    Date fechaSolicitud
    BigDecimal montoLiquidar
    Date fechaCompromiso

    String estatusContrato
    Boolean contratoMonterrey = false
    String nombreLargo
    String nombreLargoCoacreditado
    Long folioCarga

    static constraints = {
        regimenFiscal nullable: true, blank: true
        fechaContrato nullable: true, blank: true

        nombres nullable: true, blank: true
        primerApellido nullable: true, blank: true
        segundoApellido nullable: true, blank: true
        genero nullable: true, blank: true
        edad nullable: true, blank: true
        rfc nullable: true, blank: true
        fechaNacimiento nullable: true, blank: true
        curp nullable: true, blank: true
        documentoOficial nullable: true, blank: true
        claveElector nullable: true, blank: true
        telefonoFijo nullable: true, blank: true
        telefonoCelular nullable: true, blank: true
        telefonoOficina nullable: true, blank: true
        correoElectronico nullable: true, blank: true
        estatusContrato nullable: true, blank: true

        nombresCoacreditado nullable: true, blank: true
        primerApellidoCoacreditado nullable: true, blank: true
        segundoApellidoCoacreditado nullable: true, blank: true
        generoCoacreditado nullable: true, blank: true
        rfcCoacreditado nullable: true, blank: true
        edadCoacreditado nullable: true, blank: true
        documentoOficialCoacreditado nullable: true, blank: true
        fechaNacimientoCoacreditado nullable: true, blank: true
        curpCoacreditado nullable: true, blank: true
        claveElectorCoacreditado nullable: true, blank: true
        telefonoFijoCoacreditado nullable: true, blank: true
        telefonoCelularCoacreditado nullable: true, blank: true
        telefonoOficinaCoacreditado nullable: true, blank: true
        correoElectronicoCoacreditado nullable: true, blank: true

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
//        proveedor1 nullable: true, blank: true
        gps2 nullable: true, blank: true
//        proveedor2 nullable: true, blank: true
        gps3 nullable: true, blank: true
//        proveedor3 nullable: true, blank: true
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
        razonesSociales nullable: true, blank: true

        calificacionCliente nullable: true, blank: true
        numeroContrato nullable: true, blank: true
        contratoPrueba nullable: true, blank: true
        montoTransferencia nullable: true, blank: true
        detalleDescuentos nullable: true, blank: true
        fechaSolicitud nullable: true, blank: true
        montoLiquidar nullable: true, blank: true
        fechaCompromiso nullable: true, blank: true
        nombreLargo blank: true, nullable: true
        nombreLargoCoacreditado blank: true, nullable: true
        folioCarga blank: true, nullable: true
        contratoMonterrey blank: true, nullable: true
    }

    static mapping = {
        table('Contrato')
        version(false)
        id generator: 'identity'
        regimenFiscal name: 'regimenFiscal', column: 'regimenFiscal'
        fechaContrato name: 'fechaContrato', column: 'fechaContrato'

        nombres name: 'nombres', column: 'nombres'
        primerApellido name: 'primerApellido', column: 'primerApellido'
        segundoApellido name: 'segundoApellido', column: 'segundoApellido'
        genero name: 'genero', column: 'genero'
        rfc name: 'rfc', column: 'rfc'
        edad name: 'edad', column: 'edad'
        fechaNacimiento name: 'fechaNacimiento', column: 'fechaNacimiento'
        curp name: 'curp', column: 'curp'
        documentoOficial name: 'documentoOficial', column: 'documentoOficial'
        claveElector name: 'claveElector', column: 'claveElector'
        telefonoFijo name: 'telefonoFijo', column: 'telefonoFijo'
        telefonoCelular name: 'telefonoCelular', column: 'telefonoCelular'
        telefonoOficina name: 'telefonoOficina', column: 'telefonoOficina'
        correoElectronico name: 'correoElectronico', column: 'correoElectronico'
        estatusContrato name: 'estatusContrato', column: 'estatusContrato'

        nombresCoacreditado name: 'nombresCoacreditado', column: 'nombresCoacreditado'
        primerApellidoCoacreditado name: 'primerApellidoCoacreditado', column: 'primerApellidoCoacreditado'
        segundoApellidoCoacreditado name: 'segundoApellidoCoacreditado', column: 'segundoApellidoCoacreditado'
        generoCoacreditado name: 'generoCoacreditado', column: 'generoCoacreditado'
        rfcCoacreditado name: 'rfcCoacreditado', column: 'rfcCoacreditado'
        edadCoacreditado name: 'edadCoacreditado', column: 'edadCoacreditado'
        fechaNacimientoCoacreditado name: 'fechaNacimientoCoacreditado', column: 'fechaNacimientoCoacreditado'
        curpCoacreditado name: 'curpCoacreditado', column: 'curpCoacreditado'
        documentoOficialCoacreditado name: 'documentoOficialCoacreditado', column: 'documentoOficialCoacreditado'
        claveElectorCoacreditado name: 'claveElectorCoacreditado', column: 'claveElectorCoacreditado'
        telefonoFijoCoacreditado name: 'telefonoFijoCoacreditado', column: 'telefonoFijoCoacreditado'
        telefonoCelularCoacreditado name: 'telefonoCelularCoacreditado', column: 'telefonoCelularCoacreditado'
        telefonoOficinaCoacreditado name: 'telefonoOficinaCoacreditado', column: 'telefonoOficinaCoacreditado'
        correoElectronicoCoacreditado name: 'correoElectronicoCoacreditado', column: 'correoElectronicoCoacreditado'

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
//        proveedor1 name: 'proveedor1', column: 'proveedor1'
        gps2 name: 'gps2', column: 'gps2'
//        proveedor2 name: 'proveedor2', column: 'proveedor2'
        gps3 name: 'gps3', column: 'gps3'
//        proveedor3 name: 'proveedor3', column: 'proveedor3'
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
        razonesSociales name: 'razonesSociales', column: 'razonesSociales'

        calificacionCliente name: 'calificacionCliente', column: 'calificacionCliente'
        numeroContrato name: 'numeroContrato', column: 'numeroContrato'
        contratoPrueba name: 'contratoPrueba', column: 'contratoPrueba'
        montoTransferencia name: 'montoTransferencia', column: 'montoTransferencia'
        detalleDescuentos name: 'detalleDescuentos', column: 'detalleDescuentos'
        fechaSolicitud name: 'fechaSolicitud', column: 'fechaSolicitud'
        montoLiquidar name: 'montoLiquidar', column: 'montoLiquidar'
        fechaCompromiso name: 'fechaCompromiso', column: 'fechaCompromiso'
        nombreLargo name: 'nombreLargo', column: 'nombreLargo'
        nombreLargoCoacreditado name: 'nombreLargoCoacreditado', column: 'nombreLargoCoacreditado'
        folioCarga name: 'folioCarga', column: 'folioCarga'
        contratoMonterrey name: 'contratoMonterrey', column: 'contratoMonterrey'
    }
}
