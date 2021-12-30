package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.util.preparePasswordRepresentation
import com.example.mBankAuthorization.util.prepareUserRepresentation
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.authorization.client.AuthzClient
import org.keycloak.authorization.client.Configuration
import org.keycloak.exceptions.TokenNotActiveException
import org.keycloak.representations.AccessTokenResponse
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
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
        val keycloak: Keycloak
) : AuthorizationService {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val CLIENT_ROLE = "ROLE_CLIENT"

    @Throws(Exception::class)
    override fun registerClient(request: User): Response {

        val user = prepareUserRepresentation(request/*, password*/)

        val response = keycloak
                .realm(realm)
                .users()
                .create(user)

        val userId = CreatedResponseUtil.getCreatedId(response)

        log.info("Created userId {}", userId)

        val realmResource = keycloak.realm(realm);
        val userResource = realmResource
                .users()
                .get(userId)

        if (response.status == HttpStatus.CREATED.value()) {

            val password = preparePasswordRepresentation(request.password)
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

    override fun signIn(user: User): AccessTokenResponse {

        val clientCredentials: MutableMap<String, Any> = HashMap()
        clientCredentials["secret"] = secret
        clientCredentials["grant_type"] = OAuth2Constants.PASSWORD

        val configuration = Configuration(authURL, realm, clientId, clientCredentials, null)
        val authClient: AuthzClient = AuthzClient.create(configuration)

        val response = authClient.obtainAccessToken(user.username, user.password)

        if(response.error != null && response.error != ""){
            log.error("Error caused by {} trying to get keycloak token ",response.error)
            throw NoContentException(response.errorDescription)
        }
        return response
    }
}