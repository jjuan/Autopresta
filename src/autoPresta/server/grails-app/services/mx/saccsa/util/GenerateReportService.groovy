package mx.saccsa.util


import grails.gorm.transactions.Transactional

@Transactional
class GenerateReportService {

    def jasperService
    def grailsApplication

    def createReport(def request, def response, def params, def model){
        params.locale= new Locale("es","MX")

        String imagen = grailsApplication.config.jasper.images +grailsApplication.config.logo.name +"."+grailsApplication.config.logo.ext
        def result = getApplicationContext().getResource(imagen)
        params.imagen = result.getFile().path
        params.styleName = grailsApplication.config.reporte.style
        params.titulo = grailsApplication.config.titulo
        params.reporteTitulo = grailsApplication.config.reporte.titulo
        params.reporteSubTitulo = grailsApplication.config.reporte.subTitulo
        params.reportePie = grailsApplication.config.reporte.pie
        params.reporteSubpie = grailsApplication.config.reporte.subpie
        params.urlCliente = grailsApplication.config.url.cliente
        def reportDef = jasperService.buildReportDefinition(params, request.getLocale(), model)
        if (!reportDef.fileFormat.inline && !reportDef.parameters._inline) {
            response.setHeader("Content-disposition", "attachment; filename=" + (reportDef.parameters._name ?: reportDef.name) + "." + reportDef.fileFormat.extension);
            response.contentType = reportDef.fileFormat.mimeTyp
            response.characterEncoding = "UTF-8"
            response.outputStream << reportDef.contentStream.toByteArray()
        }
    }

    def createReportBuild( def params, def model){
        params.locale= new Locale("es","MX")
        String imagen = grailsApplication.config.jasper.images +grailsApplication.config.logo.name +"."+grailsApplication.config.logo.ext
        def result = getApplicationContext().getResource(imagen)
        params.imagen = result.getFile().path

        params.styleName = grailsApplication.config.reporte.style
        params.titulo = grailsApplication.config.titulo
        params.reporteTitulo = grailsApplication.config.reporte.titulo
        params.reporteSubTitulo = grailsApplication.config.reporte.subTitulo
        params.reportePie = grailsApplication.config.reporte.pie
        params.reporteSubpie = grailsApplication.config.reporte.subpie
        params.urlCliente = grailsApplication.config.url.cliente
        def reportDef = jasperService.buildReportDefinition(params,params.locale, model)
        reportDef
    }
}
