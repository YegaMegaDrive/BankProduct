package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.User
import org.keycloak.representations.idm.RoleRepresentation
import javax.ws.rs.core.Response

interface ClientService {

    fun assignRole(username: String, roleRepresentation: RoleRepresentation)
    fun assignToGroup(username: String, groupId: String)
/*    fun createClient (request: User): Response*/
    fun createDefaultClient(request: User): Response
}