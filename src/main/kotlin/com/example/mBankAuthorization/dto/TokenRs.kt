package com.example.mBankAuthorization.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.keycloak.representations.AccessTokenResponse

class TokenRs(accessTokenResponse: AccessTokenResponse) {
    @JsonProperty("access_token")
    var token: String? = null

    @JsonProperty("expires_in")
    var expiresIn: Long = 0

    @JsonProperty("refresh_token")
    var refreshToken: String? = null

    @JsonProperty("refresh_expires_in")
    var refreshExpiresIn: Long = 0

    init {
        token = accessTokenResponse.token
        expiresIn = accessTokenResponse.expiresIn
        refreshToken = accessTokenResponse.refreshToken
        refreshExpiresIn = accessTokenResponse.refreshExpiresIn
    }
}