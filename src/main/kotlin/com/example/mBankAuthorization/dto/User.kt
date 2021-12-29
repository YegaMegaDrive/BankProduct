package com.example.mBankAuthorization.dto

data class User(
        val username: String, val password: String, val firstname: String = "", val lastname: String = "",
        val email:String = "")