package com.example.chatup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.repository.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {

    // ============== Firebase auth ==============
    val auth = Firebase.auth

    // ============== Repository and livedata ===============
    private val repository = AuthRepository()
    private val _resetPasswordResult = MutableLiveData<Result<String>>()
    val resetPasswordResult: LiveData<Result<String>> = _resetPasswordResult

    // ============== User registration ==============
    fun register(
        email: String,
        password: String,
        callback: (Task<AuthResult>) -> Unit
    ) {
        repository.register(email, password, callback)
    }

    // ============= Login for email and password =============
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

    // ============== Google sign-in ==============
    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit,onFailure: (Exception) -> Unit){

        val credential = GoogleAuthProvider.getCredential(idToken,null)

        auth.signInWithCredential(credential).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    // ============== Sign out ==============
    fun signOut(){
        auth.signOut()
    }

    // =============== Reset password ==============
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

    // ============== Get current user ==============
    fun getCurrentUser() = auth.currentUser
}