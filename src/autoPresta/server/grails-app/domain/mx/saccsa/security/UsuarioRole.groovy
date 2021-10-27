package mx.saccsa.security

import grails.compiler.GrailsCompileStatic
import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import org.codehaus.groovy.util.HashCodeHelper

@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class UsuarioRole implements Serializable {

	private static final long serialVersionUID = 1

	Usuario usuario
	Role role

	@Override
	boolean equals(other) {
		if (other instanceof UsuarioRole) {
			other.usuarioId == usuario?.id && other.roleId == role?.id
		}
	}

	@Override
	int hashCode() {
		int hashCode = HashCodeHelper.initHash()
		if (usuario) {
			hashCode = HashCodeHelper.updateHash(hashCode, usuario.id)
		}
		if (role) {
			hashCode = HashCodeHelper.updateHash(hashCode, role.id)
		}
		hashCode
	}

	static UsuarioRole get(long usuarioId, long roleId) {
		criteriaFor(usuarioId, roleId).get()
	}

	static boolean exists(long usuarioId, long roleId) {
		criteriaFor(usuarioId, roleId).count()
	}

	private static DetachedCriteria criteriaFor(long usuarioId, long roleId) {
		UsuarioRole.where {
			usuario == Usuario.load(usuarioId) &&
					role == Role.load(roleId)
		}
	}

	static UsuarioRole create(Usuario usuario, Role role, boolean flush = false) {
		def instance = new UsuarioRole(usuario: usuario, role: role)
		instance.save(flush: flush)
		instance
	}

	static boolean remove(Usuario u, Role r) {
		if (u != null && r != null) {
			UsuarioRole.where { usuario == u && role == r }.deleteAll()
		}
	}

	static int removeAll(Usuario u) {
		u == null ? 0 : UsuarioRole.where { usuario == u }.deleteAll() as int
	}

	static int removeAll(Role r) {
		r == null ? 0 : UsuarioRole.where { role == r }.deleteAll() as int
	}

	static constraints = {
		usuario nullable: false
		role nullable: false, validator: { Role r, UsuarioRole ur ->
			if (ur.usuario?.id) {
				if (UsuarioRole.exists(ur.usuario.id, r.id)) {
					return ['userRole.exists']
				}
			}
		}
	}

	static mapping = {
		table "USUARIOROLE"
		id composite: ['usuario', 'role']
		version false
	}
}
