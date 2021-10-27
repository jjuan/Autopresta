grails.plugin.springsecurity.password.algorithm='bcrypt'
grails.plugin.springsecurity.userLookup.userDomainClassName = 'mx.saccsa.security.Usuario'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'mx.saccsa.security.UsuarioRole'
grails.plugin.springsecurity.authority.className = 'mx.saccsa.security.Role'
grails.plugin.springsecurity.securityConfigType = 'InterceptUrlMap'
grails.plugin.springsecurity.interceptUrlMap = [
		[pattern: '/',               access: ['permitAll']],
		[pattern: '/error',          access: ['permitAll']],
		[pattern: '/index',          access: ['permitAll']],
		[pattern: '/index.gsp',      access: ['permitAll']],
		[pattern: '/shutdown',       access: ['permitAll']],
		[pattern: '/assets/**',      access: ['permitAll']],
		[pattern: '/**/js/**',       access: ['permitAll']],
		[pattern: '/**/css/**',      access: ['permitAll']],
		[pattern: '/**/images/**',   access: ['permitAll']],
		[pattern: '/**/favicon.ico', access: ['permitAll']],
		[pattern: '/login',          access: ['permitAll']],
		[pattern: '/login/**',       access: ['permitAll']],
		[pattern: '/logout',         access: ['permitAll']],
		[pattern: '/logout/**',      access: ['permitAll']],
		[pattern: '/h2-console**',	 access: ['permitAll']],
		[pattern:'/**',              access:['isFullyAuthenticated()']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
		[pattern: '/assets/**',      filters: 'none'],
		[pattern: '/**/js/**',       filters: 'none'],
		[pattern: '/**/css/**',      filters: 'none'],
		[pattern: '/**/images/**',   filters: 'none'],
		[pattern: '/**/favicon.ico', filters: 'none'],
		[pattern: '/api/**', 		 filters:'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'],
		[pattern: '/**',			 filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter']
]

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
		[pattern: '/',               access: ['permitAll']],
		[pattern: '/error',          access: ['permitAll']],
		[pattern: '/index',          access: ['permitAll']],
		[pattern: '/index.gsp',      access: ['permitAll']],
		[pattern: '/shutdown',       access: ['permitAll']],
		[pattern: '/assets/**',      access: ['permitAll']],
		[pattern: '/**/js/**',       access: ['permitAll']],
		[pattern: '/**/css/**',      access: ['permitAll']],
		[pattern: '/**/images/**',   access: ['permitAll']],
		[pattern: '/**/favicon.ico', access: ['permitAll']]
]

grails.plugin.springsecurity.rememberMe.persistent = false
grails.plugin.springsecurity.rest.login.useJsonCredentials = true
grails.plugin.springsecurity.rest.login.failureStatusCode = 401

grails.plugin.springsecurity.rest.token.storage.useGorm = true
grails.plugin.springsecurity.rest.token.storage.gorm.tokenDomainClassName = 'mx.saccsa.security.AuthenticationToken'
grails.plugin.springsecurity.rest.token.storage.gorm.tokenValuePropertyName = 'token'
grails.plugin.springsecurity.rest.token.storage.gorm.usernamePropertyName = 'username'

grails {
	mail {
		from = 'email@saccsa.com.mx'
		host = "smtp.gmail.com"
		port = 465
		username = "pruebas@saccsa.com.mx"
		password = "Saccsa73"
		props = ["mail.smtp.auth":"true",
				 "mail.smtp.socketFactory.port":"465",
				 "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
				 "mail.smtp.socketFactory.fallback":"false"]
	}
}

bioethik.logo='Logotipo Bioethik'
bioethik.ext='jpg'
style.name='autofin'
logo.name='finanzasiQ'
logo.ext='png'
appName='FinanzasIQ'
titulo=''
reporte.style = ''
reporte.titulo=''
reporte.subTitulo=''
reporte.pie=''
reporte.subpie=''
url.cliente=''
