package mx.saccsa.security

import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.bearer.BearerTokenReader
import grails.plugin.springsecurity.rest.token.storage.TokenNotFoundException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

/*
 *  Modify by JJaun, add manager message by exception in login toke in gorm
 *  Copyright 2013-2016 Alvaro Sanchez-Mariscal <alvaro.sanchezmariscal@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import org.springframework.context.MessageSource
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.web.servlet.i18n.SessionLocaleResolver

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Handles authentication failure when BearerToken authentication is enabled.
 */
@Slf4j
class BearerTokenAuthenticationFailureHandler implements AuthenticationFailureHandler {
    BearerTokenReader tokenReader
    @Autowired
    MessageSource messageSource
    @Autowired
    SessionLocaleResolver localeResolver


    /**
     * Sends the proper response code and headers, as defined by RFC6750.
     *
     * @param request
     * @param response
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        String headerValue
        AccessToken accessToken = tokenReader.findToken(request)
        String msg
        if (accessToken) {
            headerValue = 'Bearer error="invalid_token"'
        } else {
            headerValue = 'Bearer'
        }
        msg=""
        if (e) {
            if (e instanceof AccountExpiredException) {
                msg = messageSource.getMessage("springSecurity.errors.login.expired",null,localeResolver.defaultLocale)
            }
            else if (e instanceof CredentialsExpiredException) {
                msg = messageSource.getMessage("springSecurity.errors.login.passwordExpired",null,localeResolver.defaultLocale)
            }
            else if (e instanceof DisabledException) {
                msg = messageSource.getMessage("springSecurity.errors.login.disabled",null,localeResolver.defaultLocale)
            }
            else if (e instanceof LockedException) {
                msg = messageSource.getMessage("springSecurity.errors.login.locked",null,localeResolver.defaultLocale)
            }else if(e instanceof TokenNotFoundException) {
                msg = messageSource.getMessage("springSecurity.errors.login.tokenNotFound", null, "La sesión ha expirado", request.locale)
            }else if (e instanceof SessionAuthenticationException) {
                    msg = messageSource.getMessage('springSecurity.errors.login.max.sessions.exceeded', null, "Sorry, you have exceeded your maximum number of open sessions.", request.locale)
            }else {
                msg = messageSource.getMessage("springSecurity.errors.login.fail",null,localeResolver.defaultLocale)
            }
        }
        response.addHeader('WWW-Authenticate', headerValue)
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = 'application/json'
        response.characterEncoding = 'UTF-8'
        response << '{"mensaje":"'+msg+'"}'
        log.debug "Sending status code ${response.status} and header WWW-Authenticate: ${response.getHeader('WWW-Authenticate')}"
    }
}
