package com.example.chatup.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {
    val auth = Firebase.auth

    fun register(email: String, password: String, callback: (Task<AuthResult>)-> Unit){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val uid = task.result.user?.uid ?: return@addOnCompleteListener

                val user = mapOf (
                    "email" to email,
                    "username" to email.substringBefore("@")
                )

                Firebase.firestore
                    .collection("users")
                    .document(uid)
                    .set(user)
                    .addOnCompleteListener {
                        callback(task)
                    }
            } else {
                callback(task)
            }
        }
    }

    fun login(email: String, password: String, onSuccess: ()-> Unit, onFailure: (Exception)-> Unit){
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

}