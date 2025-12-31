package com.example.chatup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.databinding.ItemConversationListLayoutBinding
import com.example.chatup.data.ConversationList

class ConversationsRecViewAdapter : RecyclerView.Adapter<ConversationsRecViewAdapter.ConversationsViewHolder>() {

    // Todo se över namnet "conversations" om det är tydligt nog eller blandas ihop med en chat conversation

    private var conversationsList: List<ConversationList> = emptyList()

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
        val conversation = conversationsList[position]
        holder.binding.tvFriendName.text = conversation.friendName
        holder.binding.tvMessageIml.text = conversation.lastMessage
    }

    override fun getItemCount(): Int {
        return conversationsList.size
    }

    inner class ConversationsViewHolder (val binding : ItemConversationListLayoutBinding ) : RecyclerView.ViewHolder (binding.root)
}