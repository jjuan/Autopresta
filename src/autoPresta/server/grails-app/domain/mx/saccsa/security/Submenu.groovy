package mx.saccsa.security

/**
 * Created by JJuan on 02/07/14.
 */
class Submenu {
    Menu menu
    String titulo
    String subtitulo
    Boolean habilitado=true
    String etiqueta
    String url
    String controller
    String controllerAs
    String template
    String sistema
    String urlAjs
    Boolean esCatalogo = false
    static constraints = {
        subtitulo nullable: true, blank: true
        titulo nullable: true, blank: true
        controller nullable: true, blank: true
        controllerAs nullable: true, blank: true
        template nullable: true, blank: true
        sistema nullable: true, blank: true
    }

    static mapping = {
        table "SUBMENU"
        version false
        //id generator: 'sequence', params:[sequence:'SUBMENU_SEQ']
        id generator: 'identity'
        controller column: "controller", name:"controller"
        controllerAs column: "controllerAs", name:"controllerAs"
        template column: "template", name:"template"
        sistema column: "sistema", name:"sistema"
        esCatalogo column: "esCatalogo", name:"esCatalogo"
    }

    String toString() {
        return "Submenu{" +
                "id=" + id +
                ", menu=" + menu +
                ", habilitado=" + habilitado +
                ", etiqueta='" + etiqueta + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
