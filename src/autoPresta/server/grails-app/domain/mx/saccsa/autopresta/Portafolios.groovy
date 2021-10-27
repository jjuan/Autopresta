package mx.saccsa.autopresta

class Portafolios {

    Integer	cvePortafolio
    String	descripcion
    Date fecha
    Mercados mercados
    static constraints = {
        cvePortafolio	nullable: true
        descripcion	nullable: true
        fecha nullable: false

    }
    static mapping = {
        table name:'PORTAFOLIOS'
        id column: 'cvePortafolio', name:'cvePortafolio', generator:'assigned'
        cvePortafolio	name: 'cvePortafolio', column: 'cvePortafolio'
        descripcion	name: 'descripcion', column: 'descripcion'
        fecha name: 'fecha', column: 'fecha'
        mercados name:"mercados", column: "mercados"
    }

    static transients = ['descLabel']
    String getDescLabel(){ cvePortafolio + " - " +descripcion   }
}
