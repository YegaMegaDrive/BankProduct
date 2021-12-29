package com.example.mBankAuthorization.dto

data class User(
        val username: String, val password: String, val firstName: String = "", val lastName: String = "",
        val email:String = "")