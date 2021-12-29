package com.example.mBankAuthorization.controller

import com.example.mBankAuthorization.dto.User
import com.example.mBankAuthorization.service.ClientService
import org.keycloak.KeycloakPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.security.Principal

@RestController
@RequestMapping("/api")
class AuthorizationController(
   @Autowired
   private val authService : ClientService
) {

    @GetMapping("/auth")
    fun getAccessToken(): String?{
        val principal = SecurityContextHolder.getContext().authentication.principal
        if(principal is KeycloakPrincipal<*>){
            return principal.name + " keycloak principal"
        }
        if(principal is Principal){
            return principal.name + " spring principal"
        }
        return "no"
    }

    @PostMapping("/register")
    fun register(@RequestBody user: User):ResponseEntity<URI>{
        val response = authService.createClient(user)
        if (response.status != 201)
            throw RuntimeException("Client was not created")
        return ResponseEntity.created(response.location).build()
    }

}
