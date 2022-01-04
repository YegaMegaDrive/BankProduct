package com.example.mBankAuthorization.controller

import com.example.mBankAuthorization.dto.TokenRs
import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.service.AuthorizationService
import org.keycloak.admin.client.Keycloak
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")
class AuthorizationController(
        @Autowired
        private val authService: AuthorizationService,
        /*@Autowired
        @Qualifier("keycloakAdmin")
        private val keycloak: Keycloak*/
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
       authService.registerClient(user)
    }

    @PostMapping("/signIn")
    @ResponseStatus(HttpStatus.FOUND)
    fun signIn(@RequestBody user: User): TokenRs {
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

    @GetMapping("/refresh/token")
    @ResponseStatus(HttpStatus.OK)
    fun refreshToken(@RequestBody user: User):TokenRs {
        return authService.refreshToken(user)
    }

}
