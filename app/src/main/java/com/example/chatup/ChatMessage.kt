package com.example.chatup

data class ChatMessage(val sendId : String, val receiveId : String, val chatMessage : String, val timeStamp : Long ) {

    constructor():this ("","","",System.currentTimeMillis())


}
