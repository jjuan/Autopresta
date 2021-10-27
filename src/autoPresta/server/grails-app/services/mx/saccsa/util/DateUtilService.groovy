package mx.saccsa.util

import grails.gorm.transactions.Transactional
import mx.saccsa.autopresta.Divisas
import mx.saccsa.autopresta.FechaInhabil

import java.text.SimpleDateFormat

@Transactional(readOnly = true)
class DateUtilService {
    /**
     * @param fechaInicial
     * @param dias
     * @param lista lista de divisa, no se ocupa para esta version
     * @return Devuelve la fecha habil siguiente de la fecha incial mas los dias de plazo
     */
    def diaHabilPlazo(Date fechaInicial, Long dias, def lista = []) {
        lista = lista.empty ? [Divisas.findByClave('MXN')] : lista;
        def esFinSemana = false;
//        Calendar c = Calendar.instance
        Calendar c = Calendar.getInstance();
        c.time = fechaInicial

        def incrementa = 0
        def signo = dias < 0 ? -1 : 1
        while (incrementa < dias.abs()) {
//            c.time = c.time + (signo * 1)
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + (signo * 1));
//            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                esFinSemana = true
            else
                esFinSemana = !FechaInhabil.findAllByFechaAndDivisaInList(c.getTime(), lista).isEmpty()
            if (esFinSemana) continue else incrementa++

        }
        c.time
    }
    /**
     * @param fechaInicial
     * @param lista lista de paises, no se ocupa para esta version
     * @return Devuelve la fecha habil siguiente de la fecha incial
     */
    def diaHabil(Date fechaInicial, def lista = []) {
        lista = lista.empty ? [Divisas.findByClave('MXN')] : lista;
        Calendar c = Calendar.instance
        c.time = fechaInicial
        while (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || !FechaInhabil.findAllByFechaAndDivisaInList(c.getTime(), lista).empty)
//            c.time = c.time + 1
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1)
        c.getTime()
    }

    /**
     * @param fechaInicial
     * @param lista lista de paises, no se ocupa para esta version
     * @return Devuelve true si fecha inicial es dia INhabil, false si es dia Habil
     */
    Boolean isDiaInHabil(Date fechaInicial, def lista = []) {
        lista = lista.empty ? [Divisas.findByClave('MXN')] : lista;

        Calendar c = Calendar.instance
        c.time = fechaInicial
        log.error "res1: " + FechaInhabil.findAllByFechaAndDivisaInList(c.getTime(), lista) + " " + lista//.empty
        if (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && FechaInhabil.findAllByFechaAndDivisaInList(c.getTime(), lista).empty)
        //if(c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && FechaInhabil.findAllByFecha(c.getTime()).empty==true)
            return false
        return true
    }

    /**
     * @param fechaInicial
     * @param lista lista de paises, no se ocupa para esta version
     * @return Devuelve true si fecha inicial es dia habil, false si no lo es
     */
    Boolean isDiaHabil(Date fechaInicial, def lista = []) {
        lista = lista.empty ? [Divisas.findByClave('MXN')] : lista;

        Calendar c = Calendar.instance
        c.time = fechaInicial
        if (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && FechaInhabil.findAllByFechaAndDivisaInList(c.getTime(), lista).empty)
        //if(c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && FechaInhabil.findAllByFecha(c.getTime()).empty==true)
            return true
        return false
    }

    /**
     * @param fechaOriginal
     * @param param valor get de Calendar
     * @return Devuelve el valor de get
     */
    def dateGetVal(Date fechaOriginal, int param) {
        def fecha = Calendar.getInstance() //En el momento
        fecha.time = fechaOriginal
        return fecha.get(param)
    }

    /** Calcula los dias comerciales entre dos fechas
     * @param fecha1
     * @param fecha2
     * @return Devuelve los dias comerciales (Integer)
     */

    Integer diasComerciales(Date vFecInput1, Date vFecInput2) {
        /*Date    vFecInput1 = new Date()    //Input
        Date    vFecInput2 = new Date()    //Input*/
        Integer vDiasRes       //Output

        Calendar vFec1 = Calendar.instance
        Calendar vFec2 = Calendar.instance
        vFec1.time = vFecInput1
        vFec2.time = vFecInput2

        BigDecimal vDias1 = 0
        BigDecimal vDias2 = 0
        BigDecimal vDiasMes = 0

        vDias1 = dateGetVal(vFec1.time, Calendar.YEAR) * 360 + vDias1 + (dateGetVal(vFec1.time, Calendar.MONTH) + 1) * 30
        vDias2 = dateGetVal(vFec2.time, Calendar.YEAR) * 360 + vDias2 + (dateGetVal(vFec2.time, Calendar.MONTH) + 1) * 30


        if (dateGetVal(vFec1.time, (Calendar.MONTH + 1)) == 2) {
            if ((dateGetVal(vFec1.time, Calendar.DAY_OF_MONTH) >= 28))
                vDiasMes = 30
            else
                vDiasMes = dateGetVal(vFec1.time, Calendar.DAY_OF_MONTH)
        } else {
            if ((dateGetVal(vFec1.time, Calendar.DAY_OF_MONTH) >= 30))
                vDiasMes = 30
            else
                vDiasMes = dateGetVal(vFec1.time, Calendar.DAY_OF_MONTH)
        }
        vDias1 = vDias1 + vDiasMes

        if (dateGetVal(vFec2.time, (Calendar.MONTH + 1)) == 2) {
            if (dateGetVal(vFec2.time, Calendar.DAY_OF_MONTH) >= 28)
                vDiasMes = 30
            else
                vDiasMes = dateGetVal(vFec2.time, Calendar.DAY_OF_MONTH)
        } else {
            if (dateGetVal(vFec2.time, Calendar.DAY_OF_MONTH) >= 30)
                vDiasMes = 30
            else
                vDiasMes = dateGetVal(vFec2.time, Calendar.DAY_OF_MONTH)
        }

        vDias2 = vDias2 + vDiasMes
        vDiasRes = vDias2 - vDias1

        return vDiasRes
    }

/**
 * @param fechaInicial
 * @param lista lista de paises, no se ocupa para esta version
 * @return Devuelve la fecha habil siguiente de la fecha incial
 */
    def diasHabilEntreDosFechas(Date fechaInicial, Date fechaFinal, def lista = []) {
        lista = lista.empty ? [Divisas.findByClave('MXN')] : lista;
        def esFinSemana = false
        Calendar c = Calendar.instance
        c.time = fechaInicial
        Calendar c2 = Calendar.instance
        c2.time = fechaFinal

        def incrementa = 0
        while (c.time != c2.time) {
            c.time = c.time + 1
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                esFinSemana = true
            else
                esFinSemana = !FechaInhabil.findAllByFechaAndDivisaInList(c.getTime(), lista).isEmpty()
            if (esFinSemana) continue else incrementa++

        }
        incrementa
    }

    def diaSemana(Date fecha) {
        def dias = [1: 'Domingo', 2: 'Lunes', 3: 'Martes', 4: 'Miercoles', 5: 'Jueves', 6: 'Viernes', 7: 'Sabado']
        def c = Calendar.getInstance()
        c.setTime(fecha)
        dias[c.get(Calendar.DAY_OF_WEEK)]
    }

    def diasPlazo(Date fecha, Integer dias) {
        Calendar c = Calendar.instance
        c.time = fecha
        c.time = c.time + dias
        c.time
    }
    /**
     * @param Fecha Inicial
     * @param Fecha Final
     * @return Regresa los dias de inflacion entre dos fechas
     */

    Integer diasInflacion(Date fechaInicial, Date fechaFinal) {

        /*output*/
        Integer resultado

        Integer diaInicial
        Integer mesInicial
        Integer anhoInicial
        Integer diaFinal
        Integer mesFinal
        Integer anhoFinal

        Calendar calFecIni = Calendar.instance
        Calendar calFecFin = Calendar.instance

        calFecIni.time = fechaInicial
        calFecFin.time = fechaFinal

        diaFinal = calFecFin.get(Calendar.DAY_OF_MONTH)
        mesFinal = calFecFin.get(Calendar.MONTH) + 1
        anhoFinal = calFecFin.get(Calendar.YEAR)

        diaInicial = calFecIni.get(Calendar.DAY_OF_MONTH)
        mesInicial = calFecIni.get(Calendar.MONTH) + 1
        anhoInicial = calFecIni.get(Calendar.YEAR)

        def meses = [1, 3, 5, 7, 8, 10, 12]
        if (diaFinal == 1) {
            diaFinal = 15
            if (mesFinal == 2 && anhoFinal % 4 != 0)
                diaFinal = 14
        } else {
            //meses 1,3,5,7,8,10,12
            if (meses.contains(mesFinal))
                diaFinal = 31
            else {
                diaFinal = 30
                if (mesFinal == 2) {
                    if (anhoFinal % 4 == 0)
                        diaFinal = 29
                    else
                        diaFinal = 28
                }
            }
        }
        if (diaInicial == 1) {
            diaInicial = 15
            if (mesInicial == 2 && anhoInicial % 4 != 0)
                diaInicial = 14
        } else {
            //meses = 1,3,5,7,8,10,12
            if (meses.contains(mesInicial))
                diaInicial = 31
            else {
                diaInicial = 30
                if (mesInicial == 2) {
                    if (anhoInicial % 4 == 0)
                        diaInicial = 29
                    else
                        diaInicial = 28
                }
            }
        }

        fechaInicial = Date.parse('dd/MM/yyyy', (diaInicial.toString() + "/" + mesInicial.toString() + "/" + anhoInicial.toString()))
        fechaFinal = Date.parse('dd/MM/yyyy', (diaFinal.toString() + "/" + mesFinal.toString() + "/" + anhoFinal.toString()))
        resultado = fechaFinal - fechaInicial
        if (resultado == 0)
            resultado = 1
        return resultado
    }

    def date(Integer dia, Integer mes,Integer anio){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
        sdf.parse("${dia}/${mes}/${anio}")
    }

    /** Regresa el ultimo dia del mes en base a una fecha
     *
     */
    Date lastDayOfMonth(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
        def fecCal = Calendar.getInstance()
        fecCal.setTime(fecha)
        fecCal.set(fecCal.get(Calendar.YEAR),(fecCal.get(Calendar.MONTH)),fecCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        fecCal.time
    }

    String emisBmv (String vEmisora, String vSerie, String vTipoInstrumento, String vInstrumento){
        String nCveIndevalTit
        String nAux = ""
        if(vTipoInstrumento.startsWith("GUBER")){
            nAux = vSerie
            if(vInstrumento == "CETES")
                nAux = "B"
            if(vInstrumento == "BONDES")
                nAux = "L"
            if(vInstrumento == "BOND182" || vInstrumento == "BONDE182")
                nAux = "LS"
            if(vInstrumento == "BONDE91" || vInstrumento == "BONDES91")
                nAux = "LP"
            if(vInstrumento.startsWith("UDIBONO"))
                nAux = vSerie
            if(vInstrumento == "AJUBONO")
                nAux = vSerie
            if(vInstrumento == "TESOBONO")
                nAux = "E"
            if(vInstrumento == "BONOS")
                nAux = "M"
            if(vInstrumento == "IBPAS" || vInstrumento == "BPAS")
                nAux = "IP"
            if(vInstrumento == "IBPAT" || vInstrumento == "BPAT")
                nAux = "IT"
            if(vInstrumento == "BREMS")
                nAux = "XA"
        }
        if(vInstrumento == "ABS" || vInstrumento == "ABS-E")
            nAux = "G"
        if(vInstrumento == "CEDES" || vInstrumento == "CDES-E")
            nAux = "F"
        if(vInstrumento.startsWith("PAGARE") || vInstrumento == "PRLV")
            nAux = "IB"
        if(vInstrumento == "BBDS")
            nAux = "J"
        if(vInstrumento == "PAPCOM")
            nAux = "D"
        if(vInstrumento == "JNAFTIIE" || vEmisora == "JNAFTIIE")
            nAux = "Q"
        if(vInstrumento == "NAFIDES" || vEmisora == "NAFIDES")
            nAux = "N"
        if(vInstrumento == "BONDIS")
            nAux = "S"
        if(vInstrumento.startsWith("PIC"))
            nAux = "PI"
        if(vInstrumento.startsWith("UMS"))
            nAux = "D1"
        if(vInstrumento == "UDICETES")
            nAux = "C"
        if(vInstrumento == "CERTIBUR")
            nAux = "91"
        if(vInstrumento == "CPO-UDIS")
            nAux = "R1"
        nCveIndevalTit = nAux + vEmisora + vSerie
        if(vTipoInstrumento.startsWith("GUBER"))
            nCveIndevalTit = nAux + "GOBFED" + vSerie
        if(vTipoInstrumento == "BANCAR")
            nCveIndevalTit = nAux + vEmisora?.substring(1) + vSerie
        if(vTipoInstrumento == "IBPAS" || vTipoInstrumento == "BPAS")
            nCveIndevalTit = nAux + "BPAS" + vSerie
        if(vTipoInstrumento == "IBPAT" || vTipoInstrumento == "BPAT")
            nCveIndevalTit = nAux + "BPAT" + vSerie
        if(vTipoInstrumento == "BPA182")
            nCveIndevalTit = nAux + "BPA182" + vSerie
        if(vTipoInstrumento.startsWith("BREMS"))
            nCveIndevalTit = nAux + "BREMS" + vSerie
        if(vTipoInstrumento.startsWith("PIC"))
            nCveIndevalTit = nAux + "PIC" + vSerie
        if(vTipoInstrumento.startsWith("UMS"))
            nCveIndevalTit = nAux + "GOBFED" + vSerie

        return nCveIndevalTit
    }

    def folioCupon(Long folio){
        def vResul = [folio:0l, consec:0]
        Integer longFolio
        longFolio = folio.toString().length()
        vResul.folio = folio.toString().substring(0,longFolio - 1).toLong()
        vResul.consec = folio.toString().substring(longFolio - 1).toInteger()
        return vResul
    }
    def diasMes(Date nfec1, Date nfec2, Date wfecha1, Date wfecha2){
        Integer wdias // dias a considerar el saldo promedio
        if(nfec1 < wfecha1) //limite inferior
            nfec1 = wfecha1
        if(nfec2 > wfecha2)  /* Limite Superior */
            nfec2 = wfecha2
        wdias   = nfec2 - nfec1
        if (wdias <= 0)
            wdias = 1
        return wdias
    }
}
