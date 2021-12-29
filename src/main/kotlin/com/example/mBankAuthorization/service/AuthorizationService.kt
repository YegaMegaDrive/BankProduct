package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.User
import org.keycloak.representations.idm.RoleRepresentation
import javax.ws.rs.core.Response

interface AuthorizationService {

    fun registerClient(request: User):Response

}