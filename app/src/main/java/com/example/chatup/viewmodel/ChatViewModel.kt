package com.example.chatup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.FirebaseManager
import com.example.chatup.data.ChatMessage
import com.example.chatup.data.User

class ChatViewModel : ViewModel() {

    private var _otherUserId = MutableLiveData<String>()

    val otherUserId : LiveData<String> get() = _otherUserId

    private val _chatMessage = MutableLiveData<List<ChatMessage>> ()

    val chatMessage : LiveData<List<ChatMessage>> get() = _chatMessage

    fun setOtherUserId (id : User){
        _otherUserId.value = id.id
    }

    fun sendMessage (chatText : String) {
        val receiverId = _otherUserId.value ?: return
        FirebaseManager.sendChatMessage(chatText, receiverId)
    }

    fun startListening (conversationId : String) {
        FirebaseManager.snapShotListener(conversationId) { chatMessages ->
            _chatMessage.value = chatMessages
        }
    }
}