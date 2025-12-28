package com.example.chatup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.databinding.ItemConversationListLayoutBinding

class ConversationsRecViewAdapter : RecyclerView.Adapter<ConversationsRecViewAdapter.ConversationsViewHolder>() {

    // Todo se över namnet "conversations" om det är tydligt nog eller blandas ihop med en chat conversation
    // <Strings> läggs sålänge för att slippa varningar,  antar att det får specificera en skapad class sen istället
    val conversationsList = emptyList<String>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationsViewHolder {
        val binding = ItemConversationListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ConversationsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ConversationsViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return conversationsList.size
    }

    inner class ConversationsViewHolder (val binding : ItemConversationListLayoutBinding ) : RecyclerView.ViewHolder (binding.root)
}