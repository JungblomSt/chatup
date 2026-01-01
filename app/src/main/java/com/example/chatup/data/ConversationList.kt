package com.example.chatup.data

import android.R

data class ConversationList (
    val conversationId : String = "",
    val lastMessage : String = "",
    val lastUpdated : Long = 0,
    //val friendUsername : String = "",
    val users : List<String> = emptyList()

)