package com.example.mBankAuthorization.util

import com.example.mBankAuthorization.dto.User
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation

fun preparePasswordRepresentation(
        password: String
): CredentialRepresentation {
    val cR = CredentialRepresentation()
    cR.isTemporary = false
    cR.type = CredentialRepresentation.PASSWORD
    cR.value = password
    return cR
}

fun prepareUserRepresentation(
        request: User,
        cR: CredentialRepresentation
): UserRepresentation {
    val newUser = UserRepresentation()
    newUser.username = request.username
    newUser.credentials = listOf(cR)
    newUser.isEnabled = true
    newUser.email = request.email
    newUser.firstName = request.firstname
    newUser.lastName = request.lastname
    return newUser
}
