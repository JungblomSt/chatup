package com.example.chatup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.FirebaseManager
import com.example.chatup.data.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration

class GroupChatViewModel : ViewModel() {

    private lateinit var conversationId: String

    private lateinit var groupMembers: List<String>

    private val _usersMap = MutableLiveData<Map<String, String>>(emptyMap())

    val usersMap: LiveData<Map<String, String>> get() = _usersMap

    private var chatListener: ListenerRegistration? = null

    private val _groupChatMessage = MutableLiveData<List<ChatMessage>>()

    val groupChatMessage: LiveData<List<ChatMessage>> get() = _groupChatMessage

    private val _chatIsOpened = MutableLiveData<Boolean>()

    fun setGroupChatOpened(isOpened: Boolean) {
        _chatIsOpened.value = isOpened
    }

    private fun isGroupChatOpened(): Boolean {
        return _chatIsOpened.value == true
    }

    fun markLastSeen(conversationId: String) {
        FirebaseManager.markLastMessageSeen(conversationId)
    }

    fun initGroupChat(convId: String?, members: List<String>) {

        if (convId == null) {
            Log.e("DEBUG_GROUP", "initGroupChat: convId is null!")
            return
        }

        conversationId = convId
        groupMembers = members

        loadUsersMap()

        Log.d(
            "DEBUG_GROUP",
            "initGroupChat: conversationId set to $conversationId with members $members"
        )

        chatListener?.remove()

        Log.d("DEBUG_GROUP", "initGroupChat: conversationId=$convId with members=$members")

        chatListener = FirebaseManager.snapShotListener(
            conversationId = conversationId,
            onUpdate = { messages ->
                Log.d("DEBUG_GROUP", "Received ${messages.size} messages from Firestore")
                Log.d("DEBUG_UI_SNAPSHOT", "Snapshot received, messages=${messages.size}")
                messages.forEach { Log.d("DEBUG_GROUP_MSG", it.toString()) }
                _groupChatMessage.postValue(messages)
            },

            chatIsOpened = { isGroupChatOpened() }
        )

    }


    private fun loadUsersMap() {
        FirebaseManager.getAllUsers(
            onComplete = { users ->
                val map: Map<String, String> = users.associate { user ->
                    Pair(user.uid, user.username.toString())
                }
                _usersMap.postValue(map)
                Log.d("DEBUG_USERS_MAP", "Loaded usersMap: $map")
            },
            onException = { e ->
                Log.e("DEBUG_USERS_MAP", e.message ?: "Error loading users")
            }
        )
    }


    fun sendGroupMessage(chatText: String) {
        if (!::conversationId.isInitialized) {
            Log.e("GROUP_VM", "GroupChatViewModel not initialized")
            return
        }

        Log.d("DEBUG_GROUP", "Sending message: '$chatText' to conversationId: $conversationId")


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