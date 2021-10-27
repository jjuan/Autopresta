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

        def contexto = grailsApplication.mainContext.getResource("${grailsApplication.config.jasper.dir.reports}")
        params.SUBREPORT_DIR = contexto.file.getAbsolutePath() + contexto.file.separator
        params._file = 'ContratoAP'
        params._format = 'PDF'
        params._name = 'Contrato Auto Presta'
        params._reporteTitulo = 'Contrato Auto Presta'
        generateReportService.createReport(request, response, params, [data: [listaDatos]])
    }


}
