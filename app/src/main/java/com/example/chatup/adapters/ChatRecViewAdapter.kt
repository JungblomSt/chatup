package com.example.chatup.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.R
import com.example.chatup.data.ChatMessage
import com.example.chatup.databinding.ItemMessageReceivedBinding
import com.example.chatup.databinding.ItemMessageSentBinding
import com.google.android.material.transition.Hold
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRecViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var chatPartnerName = ""
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var chatList = emptyList<ChatMessage>()

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding =
                ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessageSentViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            MessageReceivedViewHolder(binding)
        }
    }

    fun formatTimeStamp(timeStamp: Long): String {
        val timeStampFormatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        timeStampFormatter.timeZone = java.util.TimeZone.getDefault()
        val dateFormat = Date(timeStamp)
        return timeStampFormatter.format(dateFormat)
    }

    fun getChatPartnerName(otherUserName: String) {
        chatPartnerName = otherUserName
    }

    fun submitList(chatMessages: List<ChatMessage>) {
        chatList = chatMessages
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val chatListMessage = chatList[position]


        if (holder is MessageSentViewHolder) {
            holder.binding.tvMessageIms.text = chatListMessage.messages
            holder.binding.tvTimeStampIms.text = formatTimeStamp(chatListMessage.timeStamp)
//            holder.binding.tvFriendNameIms.text = "You"

            holder.binding.ivCheckSentIms.isVisible = false
            holder.binding.ivCheckDeliveredIms.isVisible = false

            when {
                chatListMessage.seen -> {
                    holder.binding.ivCheckDeliveredIms.setImageResource(R.drawable.seen_outline_check_small_24)
                    holder.binding.ivCheckSentIms.setImageResource(R.drawable.seen_outline_check_small_24)
                    holder.binding.ivCheckSentIms.isVisible = true
                    holder.binding.ivCheckDeliveredIms.isVisible = true

                }
                chatListMessage.delivered -> {
                    holder.binding.ivCheckDeliveredIms.setImageResource(R.drawable.outline_check_small_24)
                    holder.binding.ivCheckSentIms.setImageResource(R.drawable.outline_check_small_24)
                    holder.binding.ivCheckDeliveredIms.isVisible = true
                }
                else -> {
                    holder.binding.ivCheckSentIms.setImageResource(R.drawable.outline_check_small_24)
                    holder.binding.ivCheckSentIms.isVisible = true
                }
            }

        } else if (holder is MessageReceivedViewHolder) {
            holder.binding.tvMessageImr.text = chatListMessage.messages
            holder.binding.tvTimeStampImr.text = formatTimeStamp(chatListMessage.timeStamp)
            holder.binding.tvFriendNameImr.text = chatPartnerName
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class MessageReceivedViewHolder(val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class MessageSentViewHolder(val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root)
}