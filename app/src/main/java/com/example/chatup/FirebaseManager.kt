package com.example.chatup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class FirebaseManager {

    private val db = Firebase.firestore

    private val _chatMessage = MutableLiveData<List<ChatMessage>>()

    val chatMessage : LiveData<List<ChatMessage>> get() = _chatMessage

    private lateinit var currentUser : FirebaseUser


    fun sendChatMessage (chatText : String, receiveId : String) {

        currentUser = Firebase.auth.currentUser ?: return

        val chatMessage = ChatMessage (
            sendId = currentUser.uid,
            receiveId = receiveId,
            chatMessage = chatText,
            timeStamp = System.currentTimeMillis()
        )

    }

}