package com.example.chatup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    fun loadUserProfile() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)?.copy(uid = uid)
                        _currentUser.value = user
                    }
                }
        }
    }

    fun updateUserProfile(username: String, profileImageUrl: String?) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val updates = hashMapOf<String, Any>(
                "username" to username
            )
            if (profileImageUrl != null) {
                updates["profileImage"] = profileImageUrl
            }

            db.collection("users").document(uid).update(updates)
                .addOnSuccessListener {
                    val updatedUser = _currentUser.value?.copy(
                        username = username,
                        profileImage = profileImageUrl ?: _currentUser.value?.profileImage
                    )
                    _currentUser.value = updatedUser
                }
        }
    }
}