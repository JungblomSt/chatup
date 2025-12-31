package com.example.chatup.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.data.ChatMessage
import com.example.chatup.data.User
import com.example.chatup.databinding.ItemConversationListLayoutBinding

class ChatRecViewAdapter : RecyclerView.Adapter<ChatRecViewAdapter.ChatViewHolder>() {

    // <Strings> läggs sålänge för att slippa varningar,  antar att det får specificera en skapad class sen istället
    var chatList = emptyList<ChatMessage>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val binding = ItemConversationListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChatViewHolder(binding)
    }

    fun submitList (chatMessages : List<ChatMessage>) {
        chatList = chatMessages
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {
        val chatListMessage = chatList[position]

        holder.binding.tvMessageIml.text = chatListMessage.chatMessage

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    // Todo får ändra vart bindingen hämtas ifrån sen när vi skapat den layout filen
    //  - "ItemConversationListLayoutBinding" är då bara tillfälligt
    inner class ChatViewHolder (val binding : ItemConversationListLayoutBinding) : RecyclerView.ViewHolder (binding.root)
}