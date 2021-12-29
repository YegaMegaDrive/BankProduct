package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.User
import org.keycloak.representations.idm.RoleRepresentation
import javax.ws.rs.core.Response

interface ClientManagementService {
    fun createClient(request: User): Response
    fun assignToGroup(username: String, groupId: String)
    fun assignRole(username: String, roleRepresentation: RoleRepresentation)
}