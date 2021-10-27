package mx.saccsa.security

import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import mx.saccsa.security.Usuario
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEvent

@CompileStatic
class UsuarioPasswordEncoderListener extends AbstractPersistenceEventListener {

    @Autowired
    SpringSecurityService springSecurityService

    UsuarioPasswordEncoderListener(final Datastore datastore) {
        super(datastore)
    }

    @Override
    protected void onPersistenceEvent(AbstractPersistenceEvent event) {
        if (event.entityObject instanceof Usuario) {
            Usuario u = (event.entityObject as Usuario)
            if (u.password && (event.eventType == EventType.PreInsert || (event.eventType == EventType.PreUpdate && u.isDirty('password')))) {
                event.getEntityAccess().setProperty("password", encodePassword(u.password))
            }
        }
    }

    @Override
    boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        eventType == PreUpdateEvent || eventType == PreInsertEvent
    }

    private String encodePassword(String password) {
        springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
}
