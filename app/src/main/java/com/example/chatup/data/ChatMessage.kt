package com.example.chatup.data

data class ChatMessage(
    val senderId : String = "",
    val receiverId : String = "",
    val chatMessage : String = "",
    val timeStamp : Long = System.currentTimeMillis())