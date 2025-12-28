package com.example.chatup

data class User (
    val id : String = "",
    val email : String = "",
    val online : Boolean = false,
    val username : String? = null,
    val profileImage : String? = null

)
