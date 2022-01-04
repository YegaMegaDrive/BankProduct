package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.TokenRs
import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.util.preparePasswordRepresentation
import com.example.mBankAuthorization.util.prepareUserRepresentation
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.authorization.client.AuthzClient
import org.keycloak.authorization.client.Configuration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import javax.ws.rs.core.NoContentException
import javax.ws.rs.core.Response


@Service
class AuthorizationServiceImpl(
        @Value("\${keycloak.realm}")
        private val realm: String,
        @Value("\${keycloak.credentials.secret}")
        private val secret: String,
        @Value("\${keycloak.resource}")
        private val clientId: String,
        @Value("\${keycloak.auth-server-url}")
        private val authURL: String,
        @Autowired
        val keycloakAdmin: Keycloak,
        /*@Qualifier("keycloakUser")
        @Autowired
        var keycloakUser: Keycloak,*/
) : AuthorizationService {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val CLIENT_ROLE = "ROLE_CLIENT"

    @Throws(Exception::class)
    override fun registerClient(user: User): Response {

        val userRepresentation = prepareUserRepresentation(user/*, password*/)

        val response = keycloakAdmin
                .realm(realm)
                .users()
                .create(userRepresentation)

        val userId = CreatedResponseUtil.getCreatedId(response)

        log.info("Created userId {}", userId)

        val realmResource = keycloakAdmin.realm(realm);
        val userResource = realmResource
                .users()
                .get(userId)

        if (response.status == HttpStatus.CREATED.value()) {

            val password = preparePasswordRepresentation(user.password)
            userResource.resetPassword(password)

            val role = realmResource.roles().get(CLIENT_ROLE).toRepresentation()

            userResource.roles().realmLevel().add(listOf(role))
        }else{
            realmResource.users().delete(userId)
        }

        /* val userClientRole = realmResource.clients().get(resource)
                 .roles().get(CLIENT_ROLE).toRepresentation()
         */
/*        val rolesResource = realmResource.clients().get(resource)
                .roles()
        userResource.roles()
                .clientLevel(CLIENT_ROLE).add(listOf(userClientRole))*/
        return response
    }

    override fun signIn(user: User): TokenRs {

        val clientCredentials: MutableMap<String, Any> = HashMap()
        clientCredentials["secret"] = secret
        clientCredentials[OAuth2Constants.GRANT_TYPE] = OAuth2Constants.PASSWORD

        val configuration = Configuration(authURL, realm, clientId, clientCredentials, null)
        val authClient: AuthzClient = AuthzClient.create(configuration)

        val response = authClient.obtainAccessToken(user.username, user.password)

        //val response = keycloakUser.tokenManager().accessToken

        if(response.error != null && response.error != ""){
            log.error("Error caused by {} trying to get keycloak token ",response.error)
            throw NoContentException(response.errorDescription)
        }
        return TokenRs(response)
    }

    override fun refreshToken(user: User): TokenRs {
       val keycloakUser = KeycloakBuilder
            .builder()
            .grantType(OAuth2Constants.PASSWORD)
            .serverUrl(authURL)
            .realm(realm)
            .clientId(clientId)
            .clientSecret(secret)
            .username(user.username)
            .password(user.password)
            .build()

       val response = keycloakUser.tokenManager().refreshToken()
        return TokenRs(response)
    }
}