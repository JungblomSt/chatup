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

    private var chatListener : ListenerRegistration? = null
    private var typingListener : ListenerRegistration? = null

    private var _chatOpened = MutableLiveData<Boolean>()

    private var _isTyping = MutableLiveData<Boolean>()
    val isTyping : LiveData<Boolean> get() = _isTyping

    private var _otherUserName = MutableLiveData<String>()
    val otherUserName: LiveData<String> get() = _otherUserName
    /**
     * The unique ID of the current conversation.
     * This is generated based on the logged-in user and the selected chat partner.
     */
    private lateinit var conversationId : String

    /**
     * Holds the user ID of the person the current user is chatting with.
     */
    private var _otherUserId = MutableLiveData<String>()
    val otherUserId: LiveData<String> get() = _otherUserId

    /**
     * Holds the list of chat messages for the current conversation.
     * The UI observes this to update the RecyclerView.
     */
    private val _chatMessage = MutableLiveData<List<ChatMessage>>()
    val chatMessage: LiveData<List<ChatMessage>> get() = _chatMessage

    /**
     * Holds a list of all registered users.
     */
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    fun setChatOpened (isOpened : Boolean) {
        _chatOpened.value = isOpened
    }

    fun isChatOpened () : Boolean {
        return _chatOpened.value == true
    }

    fun setTyping (isTyping : Boolean) {
        FirebaseManager.setTyping(conversationId,isTyping)

    }


    /**
     * Collects all users from Firestore via FirebaseManager.
     * Updates LiveData so the UI can display the user list.
     */
    fun loadAllUsers() {
        FirebaseManager.getAllUsers({ userList ->
            _users.value = userList
        }, { e ->
            Log.e("!!!", e.message.toString())
        })

    }

    /**
     * Init a chat session with a specific user.
     * Creates a unique conversation ID and starts listening for real-time message updates from Firestore.
     *
     * @param otherUserId The user ID of the chat partner.
     */
    fun initChat(otherUserId: String) {
        _otherUserId.value = otherUserId
        conversationId = FirebaseManager.createConversationId(otherUserId)

        chatListener = FirebaseManager.snapShotListener(conversationId = conversationId,
        onUpdate = { chatMessage ->
            _chatMessage.postValue(chatMessage.toList())
        }, chatIsOpened = {isChatOpened()
        } )

        typingListener = FirebaseManager.typingSnapShotListener(conversationId, otherUserId){
            _isTyping.value = it
        }




    }

    fun setOtherUserName (otherUserName : String?) {
        _otherUserName.value = otherUserName ?: ""
    }

    /**
     * Sets the ID of the user the current user wants to chat with.
     *
     * @param userId Selected Chat partners user's ID "otherUserId".
     */
    fun setOtherUserId(userId: String) {
        _otherUserId.value = userId
    }

    /**
     * Sends a chat message to the selected user.
     *
     * @param chatText The message to send.
     */
    fun sendMessage(chatText: String) {
        val receiverId = _otherUserId.value ?: return
         FirebaseManager.sendChatMessage(chatText, receiverId)
    }

    override fun onCleared() {
        typingListener?.remove()
        typingListener = null
        chatListener?.remove()
        chatListener = null
        super.onCleared()

    }
}