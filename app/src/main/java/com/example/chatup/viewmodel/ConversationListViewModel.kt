package com.example.chatup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.data.ConversationList
import com.example.chatup.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ConversationListViewModel : ViewModel(){

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _conversationList = MutableLiveData<List<ConversationList>>()
    val conversationList: LiveData<List<ConversationList>> = _conversationList

    fun getAllCurrentUserConversationLists(){
        val currentUserId = auth.currentUser?.uid
        viewModelScope.launch {
            val users = getUsers()

            db.collection("conversation")
                .get()
                .addOnSuccessListener { snapshot ->
                    val convList = snapshot.documents.mapNotNull { doc ->
                        val conversation = doc.toObject(ConversationList::class.java)
                            ?.copy(conversationId = doc.id)
                        val friendId = conversation?.users?.first() { it != currentUserId }
                        val friend = users.first() { it.uid == friendId }
                        conversation?.friendUsername = friend.username!!
                        if (conversation!!.users.contains(currentUserId!!) && conversation.lastMessage.isNotEmpty()) conversation else null
                    }
                    _conversationList.value = convList
                }
        }
    }

    private suspend fun getUsers(): List<User>{
        val currentUserId = auth.currentUser?.uid

        val usersSnapshot = db.collection("users").get().await()
        val users = usersSnapshot.documents.mapNotNull { doc ->
            val user = doc.toObject(User::class.java)?.copy(uid = doc.id)
            if (user?.uid != currentUserId) user else null
        }

        return users
    }
}