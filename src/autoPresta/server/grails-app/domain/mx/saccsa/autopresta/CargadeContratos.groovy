package mx.saccsa.autopresta

class CargadeContratos {
    String regimenFiscal
    Date fechaContrato
    String genero
    String rfc
    String edad
    String curp
    String telefonoCelular
    String telefonoOficina
    String correoElectronico
    String anio
    String marca
    String modelo
    String versionAuto
    String color
    String placas
    BigDecimal valorDeCompra
    String montoMaximoAutorizado
    String numeroVin
    String gps1
    String gps2
    BigDecimal costoMensualInteres
    BigDecimal costoMensualMonitoreo
    BigDecimal costoMensualGPS
    BigDecimal totalAutoPresta
    BigDecimal iva
    BigDecimal costoMensualTotal
    String tipoContrato
    String referencia
    String clabe
    String numeroContrato
    String contratoPrueba
    String montoTransferencia
    String detalleDescuentos
    Date fechaSolicitud
    Date fechaCompromiso
    String contratoMonterrey
    String nombreLargo
    Boolean cargado

    static constraints = {
        regimenFiscal nullable:true
        fechaContrato nullable:true
        genero nullable:true
        rfc nullable:true
        edad nullable:true
        curp nullable:true
        telefonoCelular nullable:true
        telefonoOficina nullable:true
        correoElectronico nullable:true
        anio nullable:true
        marca nullable:true
        modelo nullable:true
        versionAuto nullable:true
        color nullable:true
        placas nullable:true
        valorDeCompra nullable:true
        montoMaximoAutorizado nullable:true
        numeroVin nullable:true
        gps1 nullable:true
        gps2 nullable:true
        costoMensualInteres nullable:true
        costoMensualMonitoreo nullable:true
        costoMensualGPS nullable:true
        totalAutoPresta nullable:true
        iva nullable:true
        costoMensualTotal nullable:true
        tipoContrato nullable:true
        referencia nullable:true
        clabe nullable:true
        numeroContrato nullable:true
        contratoPrueba nullable:true
        montoTransferencia nullable:true
        detalleDescuentos nullable:true
        fechaSolicitud nullable:true
        fechaCompromiso nullable:true
        contratoMonterrey nullable:true
        nombreLargo nullable:true
        cargado nullable:true
    }

    static mapping = {
        table('CargadeContratos')
        version(false)
        id generator: 'identity'
        regimenFiscal name: 'regimenFiscal', column: 'regimenFiscal'
        fechaContrato name: 'fechaContrato', column: 'fechaContrato'
        genero name: 'genero', column: 'genero'
        rfc name: 'rfc', column: 'rfc'
        edad name: 'edad', column: 'edad'
        curp name: 'curp', column: 'curp'
        telefonoCelular name: 'telefonoCelular', column: 'telefonoCelular'
        telefonoOficina name: 'telefonoOficina', column: 'telefonoOficina'
        correoElectronico name: 'correoElectronico', column: 'correoElectronico'
        anio name: 'anio', column: 'anio'
        marca name: 'marca', column: 'marca'
        modelo name: 'modelo', column: 'modelo'
        versionAuto name: 'versionAuto', column: 'versionAuto'
        color name: 'color', column: 'color'
        placas name: 'placas', column: 'placas'
        valorDeCompra name: 'valorDeCompra', column: 'valorDeCompra'
        montoMaximoAutorizado name: 'montoMaximoAutorizado', column: 'montoMaximoAutorizado'
        numeroVin name: 'numeroVin', column: 'numeroVin'
        gps1 name: 'gps1', column: 'gps1'
        gps2 name: 'gps2', column: 'gps2'
        costoMensualInteres name: 'costoMensualInteres', column: 'costoMensualInteres'
        costoMensualMonitoreo name: 'costoMensualMonitoreo', column: 'costoMensualMonitoreo'
        costoMensualGPS name: 'costoMensualGPS', column: 'costoMensualGPS'
        totalAutoPresta name: 'totalAutoPresta', column: 'totalAutoPresta'
        iva name: 'iva', column: 'iva'
        costoMensualTotal name: 'costoMensualTotal', column: 'costoMensualTotal'
        tipoContrato name: 'tipoContrato', column: 'tipoContrato'
        referencia name: 'referencia', column: 'referencia'
        clabe name: 'clabe', column: 'clabe'
        numeroContrato name: 'numeroContrato', column: 'numeroContrato'
        contratoPrueba name: 'contratoPrueba', column: 'contratoPrueba'
        montoTransferencia name: 'montoTransferencia', column: 'montoTransferencia'
        detalleDescuentos name: 'detalleDescuentos', column: 'detalleDescuentos'
        fechaSolicitud name: 'fechaSolicitud', column: 'fechaSolicitud'
        fechaCompromiso name: 'fechaCompromiso', column: 'fechaCompromiso'
        contratoMonterrey name: 'contratoMonterrey', column: 'contratoMonterrey'
        nombreLargo name: 'nombreLargo', column: 'nombreLargo'
        cargado name: 'cargado', column: 'cargado'
    }

}
