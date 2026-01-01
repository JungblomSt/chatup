package com.example.chatup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UsersViewModel : ViewModel(){

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun getAllUsers(){
        val currentUserId = auth.currentUser?.uid

        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val user = doc.toObject(User::class.java)?.copy(uid = doc.id)
                    if (user?.uid != currentUserId) user else null
                }
                _users.value = list
            }
    }
}