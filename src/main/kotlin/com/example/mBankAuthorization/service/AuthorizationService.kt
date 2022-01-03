package com.example.mBankAuthorization.service

import com.example.mBankAuthorization.dto.TokenRs
import com.example.mBankAuthorization.dto.User
import javax.ws.rs.core.Response

interface AuthorizationService {

    fun registerClient(request: User):Response
    fun signIn(user: User): TokenRs
    fun refreshToken(): TokenRs
}