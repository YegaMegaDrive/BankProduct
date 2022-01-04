package com.example.mBankAuthorization.config

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider
import org.keycloak.OAuth2Constants
import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springsecurity.KeycloakConfiguration
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.token.TokenManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy


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
    private val adminClientId: String,
    @Value("\${keycloak.credentials.secret}")
    private val secretKey: String,
    @Value("\${keycloak.resource}")
    private val clientId: String,
    @Value("\${keycloak.realm}")
    private val realm: String
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

  /*  @Bean
    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy {
        return NullAuthenticatedSessionStrategy()
    }*/

    override fun configure(http: HttpSecurity) {
        super.configure(http)
        http
            .cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/signIn").permitAll()
            //.antMatchers("api/refresh/token").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic().disable()
    }

    @Bean
    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy? {
        return RegisterSessionAuthenticationStrategy(SessionRegistryImpl())
    }

    //Keycloak auth exception handler
    @Bean
    @Throws(Exception::class)
    override fun keycloakAuthenticationProcessingFilter(): KeycloakAuthenticationProcessingFilter? {
        val filter = KeycloakAuthenticationProcessingFilter(authenticationManagerBean())
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy())
        return filter
    }

    @Bean("keycloakAdmin")
    fun keycloakAdmin(): Keycloak {
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

  /*  @Bean("keycloakUser")
    fun keycloakUser(): Keycloak {
        return KeycloakBuilder.builder()
            //.grantType(CLIENT_CREDENTIALS)
            .serverUrl(authUrl)
            .realm(adminRealm)
            .clientId(adminClientId)
            // .clientSecret(secretKey)
            .username(adminUsername)
            .password(adminPassword)
            .build()
    }*/

}