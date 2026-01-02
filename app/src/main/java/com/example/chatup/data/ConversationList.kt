package com.example.chatup.data

data class ConversationList (
    val conversationId : String = "",
    val lastMessage : String = "",
    val lastUpdated : Long = 0,
    var friendUsername : String = "",
    val users : List<String> = emptyList()
)