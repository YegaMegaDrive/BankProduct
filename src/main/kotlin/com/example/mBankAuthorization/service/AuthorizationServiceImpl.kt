package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.util.preparePasswordRepresentation
import com.example.mBankAuthorization.util.prepareUserRepresentation
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.ws.rs.core.Response


@Service
class AuthorizationServiceImpl(
        @Value("\${keycloak.realm}")
        private val realm: String,
        @Autowired
        val keycloak: Keycloak
) : AuthorizationService {

    private val CLIENT_ROLE = "ROLE_CLIENT"

    // Only for Admin
    @Throws(Exception::class)
    override fun registerClient(request: User): Response {
        val password = preparePasswordRepresentation(request.password)
        val user = prepareUserRepresentation(request, password)

        val response = keycloak
                .realm(realm)
                .users()
                .create(user)
        val userId = CreatedResponseUtil.getCreatedId(response)
        val realmResource = keycloak.realm(realm);
        val userResource = realmResource
                .users()
                .get(userId)
        val role = realmResource.roles().get(CLIENT_ROLE).toRepresentation()

        userResource.roles().realmLevel().add(listOf(role))

        /* val userClientRole = realmResource.clients().get(resource)
                 .roles().get(CLIENT_ROLE).toRepresentation()
         */
/*        val rolesResource = realmResource.clients().get(resource)
                .roles()
        userResource.roles()
                .clientLevel(CLIENT_ROLE).add(listOf(userClientRole))*/
        return response
    }

}