package com.example.chatup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.FirebaseManager
import com.example.chatup.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UsersViewModel : ViewModel() {

//    private val db = Firebase.firestore
//    private val auth = Firebase.auth
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users


    private var originalUserList = listOf<User>()


    fun getAllUsers() {
        FirebaseManager.getAllUsers({ userList ->
            originalUserList = userList
            _users.value = userList
        }, { e ->
            Log.e("!!!", e.message.toString())
        })
//        val currentUserId = auth.currentUser?.uid
//        Log.d("UsersViewModel", "getAllUsers: Fetching users from Firestore")
//
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { snapshot ->
//                val userList = snapshot.documents.mapNotNull { doc ->
//                    val user = doc.toObject(User::class.java)?.copy(uid = doc.id)
//                    if (user?.uid != currentUserId) user else null
//                }
//                // Spara till cachen
//                originalUserList = userList
//                _users.value = userList
//            }
//            .addOnFailureListener { exception ->
//                Log.e("UsersViewModel", "getAllUsers: Error fetching users", exception)
//            }
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

    fun createGroup (
        groupName : String,
        members : List<String>,
        onComplete : (String) -> Unit
    ) {
        FirebaseManager.createGroupConversation(
            groupName = groupName,
            members = members,
            onComplete = onComplete
        )
    }
}