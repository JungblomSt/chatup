package com.example.chatup.data

data class ChatMessage(
    val senderId : String = "",
    val receiverId : String = "",
    val messages : String = "",
    val timeStamp : Long = System.currentTimeMillis(),
    val delivered : Boolean = false,
    val seen : Boolean = false
)