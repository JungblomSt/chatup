package com.example.chatup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.data.ConversationList
import com.example.chatup.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class ConversationListViewModel : ViewModel(){

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _conversationList = MutableLiveData<List<ConversationList>>()
    val conversationList: LiveData<List<ConversationList>> = _conversationList

    fun getAllCurrentUserConversationLists(){
        val currentUserId = auth.currentUser?.uid

        db.collection("conversation")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val conversation = doc.toObject(ConversationList::class.java)?.copy(conversationId = doc.id)
                    if (conversation!!.users.contains(currentUserId!!)) conversation else null
                }
                _conversationList.value = list
            }
    }
}