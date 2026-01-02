package com.example.chatup.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.data.ChatMessage
import com.example.chatup.databinding.ItemConversationListLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRecViewAdapter : RecyclerView.Adapter<ChatRecViewAdapter.ChatViewHolder>() {

    var chatPartnerName = ""
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var chatList = emptyList<ChatMessage>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val binding = ItemConversationListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChatViewHolder(binding)
    }

    fun formatTimeStamp (timeStamp : Long) : String {
        val timeStampFormatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        timeStampFormatter.timeZone = java.util.TimeZone.getDefault()
        val dateFormat = Date(timeStamp)
        return timeStampFormatter.format(dateFormat)
    }

    fun getChatPartnerName (otherUserName : String) {
        chatPartnerName = otherUserName
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

        holder.binding.tvMessageIml.text = chatListMessage.messages

        if (currentUserId == chatListMessage.senderId){
            holder.binding.tvFriendName.text = "You"
        } else {
            holder.binding.tvFriendName.text = chatPartnerName
        }

        holder.binding.tvTimeStamp.text = formatTimeStamp(chatListMessage.timeStamp)

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    // Todo får ändra vart bindingen hämtas ifrån sen när vi skapat den layout filen
    //  - "ItemConversationListLayoutBinding" är då bara tillfälligt
    inner class ChatViewHolder (val binding : ItemConversationListLayoutBinding) : RecyclerView.ViewHolder (binding.root)
}