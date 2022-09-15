package mx.saccsa.autopresta

import groovy.time.TimeCategory

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
    def reporteCobranza(){
        def fechaFin = (!params?.fechaFin)?null:(sdf.parse(params?.fechaFin))
        def lista = reporteService.reporteCobranza(fechaFin)
        params._file = 'Cobranza'
        params._format = 'PDF'
        params._name = 'Reporte de Conciliaciones(' + sdf.format(fechaFin) + ' - ' + sdf.format(fechaFin) + ')'
        params._reporteTitulo = 'Reporte de Conciliaciones(' + sdf.format(fechaFin) + ' - ' + sdf.format(fechaFin) + ')'
        generateReportService.createReport(request, response, params, [data: lista])
    }
    def reporteMorosidad(){
        def fecha = (!params?.fecha)?null:(sdf.parse(params?.fecha))
        def diaInicio = (!params?.diaInicio)?0:new Integer(params?.diaInicio as String)
        def diaFin = (!params?.diaFin)?30:new Integer(params?.diaFin as String)
        def lista = reporteService.reporteMorosidad(fecha, params.tipoReporte as String, diaInicio, diaFin)
        switch (params.tipoReporte as String) {
            case 'Morosidad Mayor 200':
                params._file = 'morosidadMayor200'
                break
            case 'Morosidad Mayor 100':
                params._file = 'morosidadMayor'
                break
            case 'Morosidad Mayor':
                params._file = 'morosidadMayor'
                break
            case 'Morosidad Mayor 10':
                params._file = 'morosidadMayor10'
                break
            case 'Morosidad Menor 100':
                params._file = 'morosidadMenor'
                break
            case 'Morosidad Menor':
                params._file = 'morosidadMenor'
                break
            case 'Fraude':
                params._file = 'morosidadFraudes'
                break
        }


        params.tituloReporte = params.tipoReporte
        params._format = 'XLSX'
        generateReportService.createReport(request, response, params, [data: lista])
    }
}
