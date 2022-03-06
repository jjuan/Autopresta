package mx.saccsa.autopresta

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

import java.text.SimpleDateFormat

@Transactional
class ContratoService {
    def dateUtilService
    def sdf = new SimpleDateFormat('yyyy-MM-dd');

    def calcularFechaPago(Integer pago, Date... fecha) {

        Portafolios portafolio = Portafolios.load(1)
        Calendar portafolioFecha = Calendar.getInstance()
        portafolioFecha.setTime(fecha?fecha:portafolio.fecha)

        Calendar c = Calendar.getInstance()
        c.setTime(fecha?fecha:portafolio.fecha)
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + pago)

        Integer day = portafolioFecha.get(Calendar.DAY_OF_MONTH)
        Integer mes = c.get(Calendar.MONTH)
        Integer maxDay = maxDayOfMonth(mes)

        if (day == maxDay) {
            portafolioFecha.set(Calendar.MONTH, portafolioFecha.get(Calendar.MONTH) + pago)
        } else if (day > maxDay) {
            portafolioFecha.set(Calendar.DAY_OF_MONTH, maxDay)
            portafolioFecha.set(Calendar.MONTH, portafolioFecha.get(Calendar.MONTH) + pago)
        } else {
            portafolioFecha.set(Calendar.MONTH, portafolioFecha.get(Calendar.MONTH) + pago)
        }
        Date fechaPago = dateUtilService.diaHabil(sdf.parse(sdf.format(portafolioFecha.getTime())), []);
        return fechaPago
    }

    def maxDayOfMonth(Integer month) {
        Integer dia = 0
        switch (month) {
            case 0:
                dia = 31
                break;
            case 1:
                dia = 28
                break;
            case 2:
                dia = 31
                break;
            case 3:
                dia = 30
                break;
            case 4:
                dia = 31
                break;
            case 5:
                dia = 30
                break;
            case 6:
                dia = 31
                break;
            case 7:
                dia = 31
                break;
            case 8:
                dia = 30
                break;
            case 9:
                dia = 31
                break;
            case 10:
                dia = 30
                break;
            case 11:
                dia = 31
                break;
        }
        return dia
    }
}
