package com.example.chatup

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun sendPasswordReset(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ){
//        auth.setLanguageCode("sv") TODO: stopped working when I modified the mail on firebase.
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                }else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}