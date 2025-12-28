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


    fun sendChatMessage (chatText : String, receiverId : String) {

        currentUser = Firebase.auth.currentUser ?: return

        val conversationId = getConversationId(currentUser.uid, receiverId)

        val chatMessage = ChatMessage (
            senderId = currentUser.uid,
            receiverId = receiverId,
            chatMessage = chatText,
            timeStamp = System.currentTimeMillis()
        )

    }

    private fun getConversationId (user1Id : String, user2Id : String) : String {
        return listOf(user1Id, user2Id).sorted().joinToString("_")
    }

}