package com.example.chatup.viewmodel

import android.util.Log
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

    private val _users = MutableLiveData<List<User>>()

    val users : LiveData<List<User>> get() = _users

    fun loadAllUsers () {
        FirebaseManager.getAllUsers( {userList ->
            _users.value = userList
        }, {e ->
            Log.e("!!!",e.message.toString())
        } )

    }

    fun setOtherUserId (id : User){
        _otherUserId.value = id.id
    }

    fun sendMessage (chatText : String) {
        val receiverId = _otherUserId.value ?: return
        FirebaseManager.sendChatMessage(chatText, receiverId)
    }

    // Todo ändra så att hantering av Log.e görs här itsället för i FirebaseMananger för renare kod
    fun startListening (conversationId : String) {
        FirebaseManager.snapShotListener(conversationId) { chatMessages ->
            _chatMessage.value = chatMessages
        }
    }
}