package com.example.chatup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {
    val auth = Firebase.auth

    private val repository = AuthRepository()
    private val _resetPasswordResult = MutableLiveData<Result<String>>()
    val resetPasswordResult: LiveData<Result<String>> = _resetPasswordResult

    fun register(
        email: String,
        password: String,
        callback: (Task<AuthResult>) -> Unit
    ) {
        repository.register(email, password, callback)
    }

    fun login(
        email: String,
        password: String,
        onSuccess: ()-> Unit,
        onFailure: (Exception)-> Unit
    ){
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit,onFailure: (Exception) -> Unit){

        val credential = GoogleAuthProvider.getCredential(idToken,null)


        auth.signInWithCredential(credential).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun signOut(){
        auth.signOut()
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _resetPasswordResult.value = Result.failure(Exception("Enter email address"))
            return
        }
        repository.sendPasswordReset(email) { success, error ->
            if (success) {
                _resetPasswordResult.postValue(
                    Result.success("Reset email has been sent")
                )
            } else {
                _resetPasswordResult.postValue(
                    Result.failure(Exception(error ?: "Error"))
                )
            }
        }
    }
    fun getCurrentUser() = auth.currentUser

}