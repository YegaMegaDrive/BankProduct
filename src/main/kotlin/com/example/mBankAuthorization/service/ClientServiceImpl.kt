package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.User
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.ws.rs.core.Response

@Service
class ClientServiceImpl(
        @Value("\${keycloak.realm}")
        private val realm: String,
        @Autowired
        val keycloak: Keycloak
) : ClientService {

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

    // Only for Admin
    override fun createDefaultClient(request: User): Response{
        val password = preparePasswordRepresentation(request.password)
        val user = prepareUserRepresentation(request, password)

        //Todo add default user initialization

        return keycloak
                .realm(realm)
                .users()
                .create(user)
    }

    // Only for Admin
    /*override fun createClient(request: User): Response {
        val password = preparePasswordRepresentation(request.password)
        val user = prepareUserRepresentation(request, password)
        return keycloak
                .realm(realm)
                .users()
                .create(user)
    }*/

    private fun preparePasswordRepresentation(
            password: String
    ): CredentialRepresentation {
        val cR = CredentialRepresentation()
        cR.isTemporary = false
        cR.type = CredentialRepresentation.PASSWORD
        cR.value = password
        return cR
    }

    private fun prepareUserRepresentation(
            request: User,
            cR: CredentialRepresentation
    ): UserRepresentation {
        val newUser = UserRepresentation()
        newUser.username = request.username
        newUser.credentials = listOf(cR)
        newUser.isEnabled = true
        return newUser
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