package com.example.chatup.data

data class ChatMessage(
    var id : String = "",
    val senderId : String = "",
    val receiverId : String? = null,
    val messages : String = "",
    val timeStamp : Long = System.currentTimeMillis(),
    val delivered : Boolean = false,
    val seen : Boolean = false

//    val deliveredTo : List<String> = emptyList(),
//    val seenBy : List <String> = emptyList()
)