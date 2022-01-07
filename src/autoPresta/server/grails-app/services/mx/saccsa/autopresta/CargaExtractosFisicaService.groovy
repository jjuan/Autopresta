package mx.saccsa.autopresta

import grails.gorm.transactions.Transactional

import java.text.SimpleDateFormat

@Transactional
class CargaExtractosFisicaService {


    def citiFisica(def archivo, String nombreArchivo, CuentasBancarias cb, def session) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy")

        InputStream file = archivo.getInputStream()
        String separador = "\\|"
        def buffer = new BufferedReader(new InputStreamReader(file, 'UTF-8'))
        Integer cnt = 0
        Integer capturadas = 0, noCapturadas = 0
        buffer.eachLine { linea ->
            cnt++
            if (cnt < 7) return
            if (linea.trim() == '') return
            if (cnt == 8) {
                //de esta linea habria que revisar que datos se deben obtener, ya que es distinta a las siguientes
            }
            def l = linea.split(separador)
            def verificacion = LiquidacionBanco.findAllByBancoAndCuentaAndNoMovimientoAndFechaAndCargoAbonoAndAfavorAndReferenciaAndMontoAndProceso(
                    cb.banco, cb.cuenta, l[0], sdf.parse(l[1]), l[2]=='C', l[6], l[7], new BigDecimal(l[8].replace(",", "").replace("\\.", "")), 'CITI_FISICA'
            )
            if (verificacion.size() == 0) {
                LiquidacionBanco lb = new LiquidacionBanco()
                lb.banco = cb.banco
                lb.cuenta = cb.cuenta
                lb.fechaAlta = session.fecha
                lb.noMovimiento = l[0]
                lb.fecha = sdf.parse(l[1])
                lb.cargoAbono = l[2] == 'C'
                lb.sucursal = l[5]
                lb.afavor = l[6]
                lb.referencia = l[7]
                try {
                    if (l[8] != null && l[8] != "") {
                        lb.monto = new BigDecimal(l[8].replace(",", "").replace("\\.", ""))
                    }
                } catch (Exception) {

                    return (false) // STATUS CODE 422
                }
                lb.leyenda1 = l[9]

                lb.nombreArchivo = nombreArchivo
                lb.estatus = "PE"
                lb.proceso = "CITI_FISICA"
                lb.save(flush: true, failOnError: true)
                capturadas = capturadas + 1
            } else {
                noCapturadas = noCapturadas + 1
            }
        }
        return [capturadas: capturadas, noCapturadas: noCapturadas]
    }

    def bbvaFisica(def archivo, String nombreArchivo, CuentasBancarias cb, def session) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        InputStream file = archivo.getInputStream()
        String separador = "\t"
        def buffer = new BufferedReader(new InputStreamReader(file, 'UTF-8'))
        Integer cnt = 0
        Integer capturadas = 0, noCapturadas = 0
        buffer.eachLine { linea ->
            cnt++
            if (cnt < 7) return
            if (linea.trim() == '') return
            if (cnt == 7) {

            }

            def l = linea.split(separador)
            if (l.size() > 2) {
            //if (l[1] != null && l[1] != ""&&l[2] != null && l[2] != ""&&l[3] != null && l[3] != ""&&l[4] != null && l[4] != "") {
            def verificacion = LiquidacionBanco.findAllByBancoAndCuentaAndFechaAndMontoAndAfavorAndReferenciaAndProceso(
                    cb.banco, cb.cuenta, sdf.parse(l[0]),
                    l[2] != null && l[2] != ""?new BigDecimal(l[2].replace(",", "").replace("\\.", "")):new BigDecimal(l[3].replace("-", "").replace(",", "").replace("\\.", "")),
                    l[1], l[1], 'BBVA_FISICA'
            )

            if (verificacion.size() == 0) {

                LiquidacionBanco lb = new LiquidacionBanco()

                    lb.banco = cb.banco
                    lb.cuenta = cb.cuenta
                    lb.fechaAlta = session.fecha
                    if (l[0] != null && l[0] != "") {
                        lb.fecha = sdf.parse(l[0])
                    }
                    lb.nombreArchivo = nombreArchivo
                    if (l[2] != null && l[2] != "") {
                        lb.montoAbono = new BigDecimal(l[2].replace(",", "").replace("\\.", ""))
                        lb.monto = new BigDecimal(l[2].replace(",", "").replace("\\.", ""))
                        lb.cargoAbono = false
                    }

                    if (l[3] != null && l[3] != "") {
                        lb.montoCargo = new BigDecimal(l[3].replace("-", "").replace(",", "").replace("\\.", ""))
                        lb.monto = new BigDecimal(l[3].replace("-", "").replace(",", "").replace("\\.", ""))
                        lb.cargoAbono = true
                    }
                    //lb.cargoAbono = l[2] == 'A'
                    lb.afavor = l[1]
                    //lb.monto = new BigDecimal(l[8].replace(",", "").replace("\\.", ""))
                    lb.referencia = l[1]
                    lb.estatus = "PE"
                    lb.proceso = "BBVA_FISICA"
                    lb.save(flush: true, failOnError: true)
                capturadas = capturadas + 1
            } else {
                noCapturadas = noCapturadas + 1
            }
            }
        }
        return [capturadas: capturadas, noCapturadas: noCapturadas]
    }

    def banorteFisica(def archivo, String nombreArchivo, CuentasBancarias cb, def session) {
        InputStream file = archivo.getInputStream()
        String separador = "\\|"
        def buffer = new BufferedReader(new InputStreamReader(file, 'UTF-8'))
        Integer cnt = 0
        Integer capturadas = 0, noCapturadas = 0
        buffer.eachLine { linea ->
            cnt++
            if (cnt < 2) return
            if (linea.trim() == '') return
            def l = linea.split(separador)
            def verificacion = LiquidacionBanco.findAllByBancoAndCuentaAndFechaAndReferenciaAndProcesoAndAfavorAndMontoAbonoAndMontoCargo(
                    cb.banco, cb.cuenta, new Date(l[0]), l[1], 'BANORTE_FISICA', l[1], new BigDecimal(l[3].replace(",", "").replace("\\.", "")), new BigDecimal(l[2].replace("-", "").replace(",", "").replace("\\.", ""))
            )
            if (verificacion.size() == 0) {
                LiquidacionBanco lb = new LiquidacionBanco()
                lb.banco = cb.banco
                lb.cuenta = cb.cuenta
                lb.fechaAlta = session.fecha
                lb.fecha = new Date(l[0]);
                lb.nombreArchivo = nombreArchivo
                lb.referencia = l[1]
                //lb.afavor = l[2]
                //********lb.monto = new BigDecimal(l[8].replace(",","").replace("\\.",""))

                lb.montoAbono = new BigDecimal(l[3].replace(",", "").replace("\\.", ""))
                lb.montoCargo = new BigDecimal(l[2].replace("-", "").replace(",", "").replace("\\.", ""))

                if (new BigDecimal(l[2].replace("-", "").replace(",", "").replace("\\.", "")) != 0) {
                    lb.monto = new BigDecimal(l[2].replace("-", "").replace(",", "").replace("\\.", ""))
                }

                if (new BigDecimal(l[3].replace(",", "").replace("\\.", "")) != 0) {
                    lb.monto = new BigDecimal(l[3].replace(",", "").replace("\\.", ""))
                }
                lb.afavor = l[1]
                lb.estatus = "PE"
                lb.proceso = "BANORTE_FISICA"
                lb.save(flush: true, failOnError: true)
                capturadas = capturadas + 1
            } else {
                noCapturadas = noCapturadas + 1
            }
        }
        return [capturadas: capturadas, noCapturadas: noCapturadas]
    }
}
