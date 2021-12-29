package com.example.mBankAuthorization.config

import org.keycloak.OAuth2Constants
import org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS
import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springsecurity.KeycloakConfiguration
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import javax.annotation.PostConstruct

@KeycloakConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    @Value("\${keycloak.auth-server-url}")
    private val authUrl: String,
    @Value("\${admin.username}")
    private val adminUsername: String,
    @Value("\${admin.password}")
    private val adminPassword: String,
    @Value("\${admin.realm}")
    private val adminRealm: String,
    @Value("\${admin.resource}")
    private val adminClientId: String
) : KeycloakWebSecurityConfigurerAdapter() {

    /*@PostConstruct
    fun init(){
        println(realm)
        println(clientId)

    }*/

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        val keycloakAuthenticationProvider = keycloakAuthenticationProvider()
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(SimpleAuthorityMapper())
        auth.authenticationProvider(keycloakAuthenticationProvider)
    }

    @Bean
    fun keycloakConfigResolver(): KeycloakConfigResolver{
        return KeycloakSpringBootConfigResolver()
    }

    @Bean
    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy {
        return NullAuthenticatedSessionStrategy()
    }

    override fun configure(http: HttpSecurity) {
        super.configure(http)
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/register").permitAll()
            .anyRequest()
            .fullyAuthenticated()
            .and()
            .httpBasic().disable()
    }

    @Bean
    fun keycloak(): Keycloak {
        return KeycloakBuilder.builder()
            //.grantType(CLIENT_CREDENTIALS)
            .serverUrl(authUrl)
            .realm(adminRealm)
            .clientId(adminClientId)
           // .clientSecret(secretKey)
            .username(adminUsername)
            .password(adminPassword)
            .build()
    }

}