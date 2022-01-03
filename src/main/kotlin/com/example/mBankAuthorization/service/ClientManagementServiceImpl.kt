package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.util.preparePasswordRepresentation
import com.example.mBankAuthorization.util.prepareUserRepresentation
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.RoleRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import javax.ws.rs.core.Response


@Service
class ClientManagementServiceImpl(
    @Value("\${keycloak.realm}")
    private val realm: String,
    @Autowired
    @Qualifier("keycloakAdmin")
    val keycloak: Keycloak
) : ClientManagementService {

    private val log = LoggerFactory.getLogger(this.javaClass)
    // Only for Admin
    override fun createClient(request: User): Response {
        val user = prepareUserRepresentation(request/* password*/)
        val usersResource = keycloak
            .realm(realm)
            .users()

        val response = usersResource
            .create(user)


        if (response.status != HttpStatus.CREATED.value()) {

            val userId = CreatedResponseUtil.getCreatedId(response)

            log.info("Created userId {}", userId)

            val userResource = usersResource.get(userId)

            val password = preparePasswordRepresentation(request.password)
            userResource.resetPassword(password)
        }

        return response
    }

    // Only for Admin
    override fun assignToGroup(username: String, groupId: String) {
        keycloak
            .realm(realm)
            .users()
            .get(getUserIdByUsername(username))
            .joinGroup(groupId)
    }

    // Only for Admin
    override fun assignRole(username: String, roleRepresentation: RoleRepresentation) {

        keycloak
            .realm(realm)
            .users()
            .get(getUserIdByUsername(username))
            .roles()
            .realmLevel()
            .add(listOf(roleRepresentation))
    }

    private fun getUserIdByUsername(username: String): String {
        return keycloak
            .realm(realm)
            .users()
            .search(username)
            .first()
            .id
    }
}