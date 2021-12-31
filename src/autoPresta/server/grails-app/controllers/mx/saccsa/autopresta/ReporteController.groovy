package mx.saccsa.autopresta

import mx.saccsa.security.Usuario

import java.text.SimpleDateFormat

class ReporteController {
    static responseFormats = ['json', 'xml']
    ReporteService reporteService
    def generateReportService
    def mailService
    def springSecurityService
    def sdf = new SimpleDateFormat('yyyy-MM-dd')

    def contratoAutoPresta(Long id) {
        params
        def listaDatos=reporteService.colecion(id)
        Contrato contrato = Contrato.findById(id)
        def contexto = grailsApplication.mainContext.getResource("${grailsApplication.config.jasper.dir.reports}")
        params.SUBREPORT_DIR = contexto.file.getAbsolutePath() + contexto.file.separator
        String archivo
        if (contrato.regimenFiscal.clave == 'PM'){
            archivo = 'ContratoAPMoral'
        } else if (contrato.regimenFiscal.clave != 'PM' && contrato.nombresCoacreditado != null){
            archivo = 'ContratoAPCoacreditado'
        } else {
            archivo = 'ContratoAP'
        }
        params._file = archivo
        params._format = 'PDF'
        params._name = 'Contrato Auto Presta'
        params._reporteTitulo = 'Contrato Auto Presta'
        generateReportService.createReport(request, response, params, [data: [listaDatos]])
    }

    def contratosFirmados(){
        def fechaInicio = (!params?.fechaInicio)?null:(sdf.parse(params?.fechaInicio))
        def fechaFin = (!params?.fechaFin)?null:(sdf.parse(params?.fechaFin))
        def listaDatos = reporteService.contratosFirmados(fechaInicio, fechaFin)
        params._file='ContratosFirmados'
        params._format='PDF'
        params._name='Reportes Firmados'
        params._reporteTitulo='Reportes Firmados'
        generateReportService.createReport(request, response, params, [data:listaDatos])
    }

    def pagosRealizados(){
        def fechaInicio = (!params?.fechaInicio)?null:(sdf.parse(params?.fechaInicio))
        def fechaFin = (!params?.fechaFin)?null:(sdf.parse(params?.fechaFin))
        def lista = reporteService.pagosRealizados(fechaInicio, fechaFin)
        params._file = 'PagosRealizadosAP'
        params._format = 'PDF'
        params._name = 'Pagos Realizados'
        params._reporteTitulo = 'Pagos Realizados'
//        params.imagen = grailsApplication.config.grails.jasper.images+'logo-full.png'
//        generateReportService.createReport(request, response, params, [data: [lista]])
        generateReportService.createReport(request, response, params, [data: lista])
    }
}
