package mx.saccsa.util

import grails.gorm.transactions.Transactional
import mx.saccsa.autopresta.Folios

@Transactional
class FolioService {

    def sessionFactory

    Long generaFolio(String tipo){
        tipo = tipo.toUpperCase()
        Folios objFolio
        objFolio = Folios.findByCveTipo(tipo)
        if(objFolio){
            objFolio.lock()
            objFolio.refresh()
            objFolio.folio++
        }
        else {
            objFolio = new Folios()
            objFolio.cveTipo = tipo.toUpperCase()
            objFolio.folio = 1l

        }
        objFolio.save(flush: true)
        objFolio.folio
    }

}
