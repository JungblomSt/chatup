package com.example.chatup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {
    val auth = Firebase.auth

    private val repository = AuthRepository()
    private val _resetPasswordResult = MutableLiveData<Result<String>>()
    val resetPasswordResult: LiveData<Result<String>> = _resetPasswordResult

    fun register(
        email: String,
        password: String,
        callback: (Task<AuthResult>)-> Unit
    ){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { callback(it) }
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


}