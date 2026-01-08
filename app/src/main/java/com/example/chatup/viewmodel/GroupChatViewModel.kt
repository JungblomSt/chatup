package com.example.chatup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.FirebaseManager
import com.example.chatup.data.ChatMessage
import com.google.firebase.firestore.ListenerRegistration

class GroupChatViewModel : ViewModel () {

    private lateinit var conversationId : String

    private lateinit var groupMembers : List<String>

    private var chatListener : ListenerRegistration? = null

    private val _groupChatMessage = MutableLiveData<List<ChatMessage>>()
    val groupChatMessage : LiveData<List<ChatMessage>> get() = _groupChatMessage

    private val _chatIsOpened = MutableLiveData<Boolean>()

    fun setGroupChatOpened (isOpened : Boolean) {
        _chatIsOpened.value = isOpened
    }

    private fun isGroupChatOpened () : Boolean {
        return _chatIsOpened.value == true
    }

    fun initGroupChat (convId : String?, members : List<String>) {

        if (convId == null) return

        conversationId = convId
        groupMembers = members

        chatListener?.remove()

        chatListener = FirebaseManager.snapShotListener(
            conversationId = conversationId,
            onUpdate = {messages ->
                _groupChatMessage.postValue(messages)
            },
            chatIsOpened = {isGroupChatOpened()}
        )

    }

    fun sendGroupMessage (chatText : String) {
        if (!::conversationId.isInitialized) {
            Log.e("GROUP_VM", "GroupChatViewModel not initialized")
            return
        }

        FirebaseManager.sendGroupMessage(
            conversationId = conversationId,
            chatText = chatText,
            members = groupMembers
        )

    }

    override fun onCleared() {
        chatListener?.remove()
        chatListener = null
        super.onCleared()
    }




}