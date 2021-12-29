package com.example.mBankAuthorization.controller

import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.service.AuthorizationService
import org.keycloak.KeycloakPrincipal
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.keycloak.admin.client.Keycloak
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.Response

@RestController
@RequestMapping("/api")
class AuthorizationController(
        @Autowired
        private val authService: AuthorizationService,
        @Autowired
        private val keycloak: Keycloak
        ) {

    private var response: Response? = null

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

        response = authService.registerClient(user)

        if (response?.status != 201) {
            throw RuntimeException("Client was not created")
        }
    }

    @PostMapping("/authorize")
    @ResponseStatus(HttpStatus.FOUND)
    fun getToken(@RequestBody user: User, request:HttpServletRequest):String{
/*
        val principal = (request.userPrincipal as KeycloakAuthenticationToken)
                .principal
        val token = (principal as KeycloakPrincipal<*>).keycloakSecurityContext.token.

        return token*/
      /*  response = authService.registerClient(user)

        if (response?.status != 201) {
            throw RuntimeException("Client was not created")
        }*/
        return keycloak.tokenManager().accessToken.token;
    }

}
