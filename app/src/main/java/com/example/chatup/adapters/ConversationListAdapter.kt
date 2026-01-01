package com.example.chatup.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.StartMenuActivity
import com.example.chatup.data.ConversationList
import com.example.chatup.databinding.ItemConversationListLayoutBinding

class ConversationListAdapter(private var conversationList: List<ConversationList>,  private val onConversationClicked: (ConversationList) -> Unit) :
    RecyclerView.Adapter<ConversationListAdapter.ConversationListViewHolder>(){

    inner class ConversationListViewHolder (val binding : ItemConversationListLayoutBinding) : RecyclerView.ViewHolder (binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationListViewHolder {
        val binding = ItemConversationListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ConversationListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationListViewHolder, position: Int) {
        val conversation = conversationList[position]
        holder.binding.tvMessageIml.text = conversation.lastMessage
        holder.binding.tvFriendName.text = conversation.friendUsername

        holder.binding.conversationListCardView.setOnClickListener {
            onConversationClicked(conversation)
        }
    }

    override fun getItemCount() = conversationList.size

    fun update(newConvList: List<ConversationList>) {
        conversationList = newConvList
        notifyDataSetChanged()
    }
}