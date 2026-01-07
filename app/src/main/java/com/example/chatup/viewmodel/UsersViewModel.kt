package com.example.chatup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UsersViewModel : ViewModel() {

    // ============== Firebase ==============
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // ============== Livedata ==============
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    // ============== Data ==================
    private var originalUserList = listOf<User>()

    // ============== Fetch users ===========
    fun getAllUsers() {
        val currentUserId = auth.currentUser?.uid
        Log.d("UsersViewModel", "getAllUsers: Fetching users from Firestore")

        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val userList = snapshot.documents.mapNotNull { doc ->
                    val user = doc.toObject(User::class.java)?.copy(uid = doc.id)
                    if (user?.uid != currentUserId) user else null
                }
                originalUserList = userList
                _users.value = userList
            }
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _users.value = originalUserList
        } else {
            val filteredList = originalUserList.filter { user ->
                val usernameMatch = user.username?.contains(query, ignoreCase = true) == true
                val emailMatch = user.email.contains(query, ignoreCase = true)
                usernameMatch || emailMatch
            }
            _users.value = filteredList
        }
    }
}