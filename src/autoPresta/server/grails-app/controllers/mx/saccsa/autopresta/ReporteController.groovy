package mx.saccsa.autopresta

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
        params._file = 'ContratoAP'
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
        generateReportService.createReport(request, response, params, [data: lista])
    }
    def conciliaciones(){
        def fechaInicio = (!params?.fechaInicio)?null:(sdf.parse(params?.fechaInicio))
        def fechaFin = (!params?.fechaFin)?null:(sdf.parse(params?.fechaFin))
        def lista = reporteService.conciliaciones(fechaInicio, fechaFin)
        params._file = 'reporteConciliaciones'
        params._format = 'PDF'
        params._name = 'Reporte de Conciliaciones(' + sdf.format(fechaInicio) + ' - ' + sdf.format(fechaFin) + ')'
        params._reporteTitulo = 'Reporte de Conciliaciones(' + sdf.format(fechaInicio) + ' - ' + sdf.format(fechaFin) + ')'
        generateReportService.createReport(request, response, params, [data: lista])
    }
}
