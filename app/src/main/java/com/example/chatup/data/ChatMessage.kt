package com.example.chatup.data

data class ChatMessage(
    val senderId : String = "",
    val receiverId : String = "",
    val messages : String = "",
    val timeStamp : Long = System.currentTimeMillis(),
//    val delivered : Boolean = false,
//    val seen : Boolean = false,
    val deliveredTo : List<String> = emptyList(),
    val seenBy : List <String> = emptyList()
) {
    fun isDelivered (userId : String) = deliveredTo.contains(userId)
    fun isSeen (userId: String) = seenBy.contains(userId)
}