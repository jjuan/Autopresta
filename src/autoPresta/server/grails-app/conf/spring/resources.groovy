package spring

// Place your Spring DSL code here
import grails.plugin.springsecurity.SpringSecurityUtils
import mx.saccsa.security.BearerTokenAuthenticationFailureHandler
import mx.saccsa.security.GormTokenStorageService
import mx.saccsa.security.UsuarioPasswordEncoderListener
beans = {
    usuarioPasswordEncoderListener(UsuarioPasswordEncoderListener, ref('hibernateDatastore'))
    localeResolver(org.springframework.web.servlet.i18n.SessionLocaleResolver) {
        defaultLocale = new Locale("es","MX")
        java.util.Locale.setDefault(defaultLocale)
    }
    def conf = SpringSecurityUtils.securityConfig
    if (!conf || !conf.active) {
        return
    }

    restAuthenticationFailureHandler(BearerTokenAuthenticationFailureHandler){
        tokenReader = ref('tokenReader')
    }

    tokenStorageService(GormTokenStorageService) {
        userDetailsService = ref('userDetailsService')
    }
}
