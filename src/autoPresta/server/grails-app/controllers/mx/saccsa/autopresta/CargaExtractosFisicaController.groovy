package mx.saccsa.autopresta

import java.text.SimpleDateFormat

class CargaExtractosFisicaController {
    static responseFormats = ['json', 'xml']
    def sdf = new SimpleDateFormat('yyyy-MM-dd')
    def cargaExtractosFisicaService

    def edoctaFisica(){
        def file = params['file']
        if (!file) {
            response.status = 400
            respond mensaje: "El archivo es incorrecto o nulo."
            return
        }
        def banco = Banco.findById(params.banco as long)
        def cuenta = params.cuenta as long
        def nombreArchivo = file.originalFilename
        if(LiquidacionBanco.findByNombreArchivoAndFechaAlta(nombreArchivo,session.fecha)) {
            response.status = 400
            respond mensaje: "Ya se ha cargado un archivo con el nombre " +nombreArchivo
            return
        }
        CuentasBancarias cb = CuentasBancarias.findById(cuenta)
        if(!cb) {
            response.status = 400
            respond mensaje: "No se encontro la cuenta "+ nombreArchivo+" en el catalogo de cuentas bancarias."
            return
        }
        def capturas
        if(banco.descripcionCorta =="BBVA")
            capturas = cargaExtractosFisicaService.bbvaFisica(file,nombreArchivo,cb,session)
        if(banco.descripcionCorta =="Citibanamex")
            capturas = cargaExtractosFisicaService.citiFisica(file,nombreArchivo,cb,session)
        if(banco.descripcionCorta =="Banorte")
            capturas = cargaExtractosFisicaService.banorteFisica(file,nombreArchivo,cb,session)



        if (capturas.noCapturadas > 0){
            response.status = 400
            respond mensaje: "Movimientos previamente capturados: " + capturas.noCapturadas + " Movimientos capturados: " + capturas.capturadas
            return
        }
        respond mensaje:"Proceso de carga completo.", fileName: nombreArchivo, fecha:session.fecha
    }

    def resumen(String id){
        def data = LiquidacionBanco.findAllByNombreArchivo(id).collect{
            def montoCargo = 0.0
            def montoAbono = 0.0
            def tipo
            if(it.cargoAbono != null) {
                if(it.cargoAbono) {
                    montoCargo = it.monto
                    tipo = 'Cargo'
                }
                else {
                    montoAbono = it.monto
                    tipo = 'Abono'
                }
            }else {
                if(it.montoAbono == 0.0) tipo = 'Cargo'
                else tipo = 'Abono'
                montoCargo = it.montoCargo
                montoAbono = it.montoAbono
            }
            [referencia: it.leyenda1?:it.referencia,
             fecha: it.fecha,
             montoCargo: montoCargo,
             montoAbono: montoAbono,
             tipo: tipo,
             cuenta: it.cuenta,
             referenciaBancaria: it.referenciaCliente,
             descripcion: it.afavor]
        }
        respond data:data
    }

    def consultaExtractos(){
        params
        def fechaInicio = null
        def fechaFin = null
        if (params.fechaInicio != "null") {
            fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        }
        if (params.fechaFin != "null") {
            fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        }
        def razonSocial = params.razonSocial ? params.razonSocial : null
        def alias = params.alias ? params.alias : null
        def archivo = params.archivo ? params.archivo : null

        def cuentas = []
        if (razonSocial != null){
            RazonesSociales razonSocialId = RazonesSociales.findById(razonSocial as long)
            cuentas = CuentasBancarias.findAllByRazonSocial(razonSocialId).collect({[cuenta: it.cuenta]})
            if (cuentas == []){

                response.status = 400
                respond mensaje: "No se encontraron cuentas bancarias relacionadas a la Razon Social."
                return
            }
        }
        CuentasBancarias cuenta = []
        if (alias != null && alias != "null"){
            cuenta = CuentasBancarias.findById(alias as long)
        }
        def movimientos = []
        if (fechaInicio != null && fechaFin != null && razonSocial == null && alias == null && archivo == null){
            movimientos = LiquidacionBanco.findAllByFechaBetween(fechaInicio, fechaFin)
        } else  if (fechaInicio == null && fechaFin == null && razonSocial == null && alias == null && archivo != null){
            movimientos = LiquidacionBanco.findAllByNombreArchivo(archivo)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial != null && alias == null && archivo == null){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndCuentaInList(fechaInicio, fechaFin, cuentas.cuenta)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial == null && alias == null && archivo != null){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndNombreArchivo(fechaInicio, fechaFin, archivo)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial != null && alias != null && archivo == null){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndCuenta(fechaInicio, fechaFin, cuenta.cuenta)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial != null && alias == null && archivo != null && archivo != ""){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndCuentaInListAndNombreArchivo(fechaInicio, fechaFin, cuentas.cuenta, archivo)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial != null && alias != null && archivo != null && archivo != ""){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndCuentaAndNombreArchivo(fechaInicio, fechaFin, cuenta.cuenta, archivo)
        }

        def data = movimientos.collect{
            def montoCargo = 0.0
            def montoAbono = 0.0
            def tipo
            if(it.cargoAbono != null) {
                if(it.cargoAbono == true ) {
                    montoCargo = it.monto
                    tipo = 'Cargo'
                }
                else {
                    montoAbono = it.monto
                    tipo = 'Abono'
                }
            }else {
                if(it.montoAbono == 0.0) tipo = 'Cargo'
                else tipo = 'Abono'
                montoCargo = it.montoCargo
                montoAbono = it.montoAbono
            }
            [referencia: it.leyenda1?:it.referencia,
             fecha: it.fecha,
             montoCargo: montoCargo,
             montoAbono: montoAbono,
             tipoMovimiento: tipo,
             cuenta: it.cuenta,
             referenciaBancaria: it.referenciaCliente,
             descripcion: it.afavor]
        }
        respond data
    }

    def listaArchivos(){
        def fechaInicio = null
        def fechaFin = null
        if (params.fechaInicio != "null") {
            fechaInicio = params.fechaInicio ? sdf.parse(params.fechaInicio) : null
        }
        if (params.fechaFin != "null") {
            fechaFin = params.fechaFin ? sdf.parse(params.fechaFin) : null
        }
        def razonSocial = params.razonSocial ? params.razonSocial : null
        def alias = params.alias ? params.alias : null
        def archivo = params.archivo ? params.archivo : null

        def cuentas = []
        if (razonSocial != null){
            RazonesSociales razonSocialId = RazonesSociales.findById(razonSocial as long)
            cuentas = CuentasBancarias.findAllByRazonSocial(razonSocialId).collect({[cuenta: it.cuenta]})
            if (cuentas == []){
                response.status = 400
                respond mensaje: "No se encontraron cuentas bancarias relacionadas a la Razon Social."
                return
            }
        }
        CuentasBancarias cuenta = []
        if (alias != null){
            cuenta = CuentasBancarias.findById(alias as long)
        }
        def movimientos = []
        if (fechaInicio != null && fechaFin != null && razonSocial == null && alias == null && archivo == null){
            movimientos = LiquidacionBanco.findAllByFechaBetween(fechaInicio, fechaFin)
        } else  if (fechaInicio == null && fechaFin == null && razonSocial == null && alias == null && archivo != null){
            movimientos = LiquidacionBanco.findAllByNombreArchivo(archivo)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial != null && alias == null && archivo == null){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndCuentaInList(fechaInicio, fechaFin, cuentas.cuenta)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial == null && alias == null && archivo != null){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndNombreArchivo(fechaInicio, fechaFin, archivo)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial != null && alias != null && archivo == null){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndCuenta(fechaInicio, fechaFin, cuenta.cuenta)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial != null && alias == null && archivo != null && archivo != ""){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndCuentaInListAndNombreArchivo(fechaInicio, fechaFin, cuentas.cuenta, archivo)
        } else  if(fechaInicio != null && fechaFin != null && razonSocial != null && alias != null && archivo != null && archivo != ""){
            movimientos = LiquidacionBanco.findAllByFechaBetweenAndCuentaAndNombreArchivo(fechaInicio, fechaFin, cuenta.cuenta, archivo)
        }

        def data = movimientos.collect{
            [
                    id: it.nombreArchivo,
                    descripcion: it.nombreArchivo
            ]
        }.unique()
        respond data
    }
}
