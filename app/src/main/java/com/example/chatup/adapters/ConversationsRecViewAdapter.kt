package com.example.chatup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.R
import com.example.chatup.data.ChatMessage
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

        //holder.binding.tvFriendName.text = conversation.username
        holder.binding.tvMessageIml.text = conversation.lastMessage

        holder.binding.ivCheckSentIcl.isVisible = false
        holder.binding.ivCheckDeliveredIcl.isVisible = false

            when {
                conversation.lastMessageSeen -> {
                    holder.binding.ivCheckDeliveredIcl.setImageResource(R.drawable.seen_outline_check_small_24)
                    holder.binding.ivCheckSentIcl.setImageResource(R.drawable.seen_outline_check_small_24)
                    holder.binding.ivCheckSentIcl.isVisible = true
                    holder.binding.ivCheckDeliveredIcl.isVisible = true

                }
                conversation.lastMessageDelivered -> {
                    holder.binding.ivCheckDeliveredIcl.setImageResource(R.drawable.outline_check_small_24)
                    holder.binding.ivCheckSentIcl.setImageResource(R.drawable.outline_check_small_24)
                    holder.binding.ivCheckDeliveredIcl.isVisible = true
                    holder.binding.ivCheckSentIcl.isVisible = true
                }
                else -> {
                    holder.binding.ivCheckSentIcl.setImageResource(R.drawable.outline_check_small_24)
                    holder.binding.ivCheckSentIcl.isVisible = true
                }

        }


    }

    override fun getItemCount(): Int {
        return conversationsList.size
    }

    inner class ConversationsViewHolder (val binding : ItemConversationListLayoutBinding ) : RecyclerView.ViewHolder (binding.root)
}