package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.util.preparePasswordRepresentation
import com.example.mBankAuthorization.util.prepareUserRepresentation
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.RoleRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.ws.rs.core.Response

@Service
class ClientManagementServiceImpl(
        @Value("\${keycloak.realm}")
        private val realm: String,
        @Autowired
        val keycloak: Keycloak
) : ClientManagementService {

    // Only for Admin
    override fun createClient(request: User): Response {
        val password = preparePasswordRepresentation(request.password)
        val user = prepareUserRepresentation(request, password)
        return keycloak
                .realm(realm)
                .users()
                .create(user)
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