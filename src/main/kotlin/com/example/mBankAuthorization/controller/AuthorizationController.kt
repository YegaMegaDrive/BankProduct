package com.example.mBankAuthorization.controller

import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.service.AuthorizationService
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.authorization.client.AuthzClient
import org.keycloak.representations.AccessTokenResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.NoContentException
import javax.ws.rs.core.Response


@RestController
@RequestMapping("/api")
class AuthorizationController(
        @Autowired
        private val authService: AuthorizationService,
        @Autowired
        private val keycloak: Keycloak
        ) {

  /*  @GetMapping("/auth")
    fun getAccessToken(): String? {
        val principal = SecurityContextHolder.getContext().authentication.principal
        if (principal is KeycloakPrincipal<*>) {
            return principal.name + " keycloak principal"
        }
        if (principal is Principal) {
            return principal.name + " spring principal"
        }
        return "no"
    }*/

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody user: User){
       val response = authService.registerClient(user)
    }

    @PostMapping("/authorize")
    @ResponseStatus(HttpStatus.FOUND)
    fun signIn(@RequestBody user: User): AccessTokenResponse {
/*
        val principal = (request.userPrincipal as KeycloakAuthenticationToken)
                .principal
        val token = (principal as KeycloakPrincipal<*>).keycloakSecurityContext.token.

        return token*/
      /*  response = authService.registerClient(user)

        if (response?.status != 201) {
            throw RuntimeException("Client was not created")
        }*/
        /*return keycloak.tokenManager().accessToken.token;*/
        return authService.signIn(user)
    }

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    fun test(): String{
        return "all works"
    }

}
