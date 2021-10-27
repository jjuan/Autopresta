package mx.saccsa.security

/**
 * Created by JJuan on 02/07/14.
 */
class Menu {
    String icono
    String nombre
    String etiqueta
    Boolean habilitado
    static hasMany = [submenu:Submenu]
    static constraints = {
        icono nullable: true,blank: true
    }
    static mapping = {
        table "MENU"
        version false
        //id generator: 'native', params:[sequence:'MENU_SEQ']
        id generator: 'identity'
        submenu sort: 'etiqueta', order: 'asc'
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Menu)) return false

        Menu menu = (Menu) o

        if (id != menu.id) return false
        if (version != menu.version) return false

        return true
    }

    int hashCode() {
        int result
        result = id.hashCode()
        result = 31 * result + version.hashCode()
        return result
    }
}
