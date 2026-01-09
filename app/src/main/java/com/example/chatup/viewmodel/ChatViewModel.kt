package com.example.chatup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.FirebaseManager
import com.example.chatup.data.ChatMessage
import com.example.chatup.data.User
import com.google.firebase.firestore.ListenerRegistration

class ChatViewModel : ViewModel() {


    private var chatListener: ListenerRegistration? = null
    private var typingListener: ListenerRegistration? = null

    private val _chatOpened = MutableLiveData<Boolean>()

    private val _isTyping = MutableLiveData<Boolean>()
    val isTyping: LiveData<Boolean> get() = _isTyping

    private val _otherUserName = MutableLiveData<String>()
    val otherUserName: LiveData<String> get() = _otherUserName

    private val _conversationId = MutableLiveData<String>()
    val conversationId: LiveData<String> get() = _conversationId

    private val _otherUserId = MutableLiveData<String>()

    private val _chatMessage = MutableLiveData<List<ChatMessage>>()
    val chatMessage: LiveData<List<ChatMessage>> get() = _chatMessage

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    /**
     * Sets the current conversation ID.
     * This is now LiveData and can be observed in the Activity.
     */
    fun setConversationId(otherUserId: String) {
        _conversationId.value = FirebaseManager.createConversationId( otherUserId)
    }

    fun setOtherUserName(otherUserName: String?) {
        _otherUserName.value = otherUserName ?: ""
    }

    fun setOtherUserId(userId: String) {
        _otherUserId.value = userId
    }

    fun setChatOpened(isOpened: Boolean) {
        _chatOpened.value = isOpened
    }

    fun isChatOpened(): Boolean {
        return _chatOpened.value == true
    }

    fun setTyping(isTyping: Boolean) {
        _conversationId.value?.let { conversationId ->
            FirebaseManager.setTyping(conversationId, isTyping)
        }
    }

    /**
     * Initializes chat for a specific user.
     * Starts listening to messages and typing status.
     */
    fun initChat(otherUserId: String) {
        _otherUserId.value = otherUserId

        _conversationId.value = FirebaseManager.createConversationId( otherUserId)


        chatListener = FirebaseManager.snapShotListener(
            conversationId = _conversationId.value!!,
            onUpdate = { messages ->
                _chatMessage.postValue(messages.toList())
            },
            chatIsOpened = { isChatOpened() }
        )


        typingListener = FirebaseManager.typingSnapShotListener(
            _conversationId.value!!,
            otherUserId
        ) { typing ->
            _isTyping.value = typing
        }
    }

    /**
     * Sends a message to the other user.
     */
    fun sendMessage(chatText: String) {
        val receiverId = _otherUserId.value ?: return
        FirebaseManager.sendChatMessage(chatText, receiverId)
    }

    /**
     * Marks all messages in this conversation as seen by the current user
     * and updates delivered/seen state for UI.
     */
    fun markChatAsSeen(conversationId: String) {
        FirebaseManager.markSeenPrivateChat(conversationId)
        FirebaseManager.markDeliveredPrivateChats(conversationId)
    }

    /**
     * Checks messages for delivery status (for the current user)
     */
    fun checkDeliveredMessage(conversationId: String) {
        FirebaseManager.markDeliveredPrivateChats(conversationId)
    }

    /**
     * Clears listeners when ViewModel is destroyed
     */
    override fun onCleared() {
        chatListener?.remove()
        chatListener = null
        typingListener?.remove()
        typingListener = null
        super.onCleared()
    }

}