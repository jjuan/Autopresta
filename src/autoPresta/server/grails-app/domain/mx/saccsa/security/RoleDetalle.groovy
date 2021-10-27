package mx.saccsa.security

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode

@GrailsCompileStatic
@EqualsAndHashCode(includes=['role', 'submenu'])
class RoleDetalle implements Serializable {

    Role role
    Submenu submenu

    Boolean crear = true
    Boolean borrar = true
    Boolean editar = true

    String toString(){
        "[Role:"+role.authority+",subMenu:"+submenu.url+"]"
    }
    boolean equals(other) {
        if (!(other instanceof RoleDetalle)) {
            return false
        }

        other.submenu?.id == submenu?.id &&
                other.role?.id == role?.id
    }


    static RoleDetalle get(long submenuId, long roleId) {
        RoleDetalle.where {
            submenu == submenu.load(submenuId) &&
                    role == Role.load(roleId)
        }.get()
    }

    static boolean exists(long submenuId, long roleId) {
        RoleDetalle.where {
            submenu == Submenu.load(submenuId) &&
                    role == Role.load(roleId)
        }.count() > 0
    }

    static RoleDetalle create(Submenu submenu, Role role, boolean  crear = true, boolean editar = true, boolean borrar = true, boolean flush = false) {
        def instance = new RoleDetalle(submenu: submenu, role: role,crear: crear,editar: editar,borrar: borrar)
        instance.save(flush: flush, insert: true,failOnError: true)
        instance
    }

    static boolean remove(Submenu s, Role r, boolean flush = false) {
        if (s == null || r == null) return false

        Number rowCount = RoleDetalle.where {
            submenu == Submenu.load(s.id) &&
                    role == Role.load(r.id)
        }.deleteAll()

        //if (flush) { UsuarioRole.withSession { this.flush() } }

        rowCount > 0
    }

    static void removeAll(Submenu s, Role r, boolean flush = false) {
        if (s == null || r == null) return

        RoleDetalle.where {
            submenu == Submenu.load(s.id) && role == Role.load(r.id)
        }.deleteAll()
    }

    static void removeAll(Role r, boolean flush = false) {
        if (r == null) return

        RoleDetalle.where {
            role == Role.load(r.id)
        }.deleteAll()

        //if (flush) { RoleDetalle.withSession { it.flush() } }
    }

    static constraints = {

        crear nullable: true
        editar nullable: true
        borrar nullable: true
    }

    static mapping = {
        table "ROLEDETALLE"
        version false
        id composite: ['role', 'submenu']
        version false
    }
}
