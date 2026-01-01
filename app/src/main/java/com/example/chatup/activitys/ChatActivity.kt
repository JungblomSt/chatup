package com.example.chatup.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatup.adapters.ChatRecViewAdapter
import com.example.chatup.databinding.ActivityChatBinding
import com.example.chatup.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var chatViewModel: ChatViewModel

    private lateinit var adapter: ChatRecViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        adapter = ChatRecViewAdapter()

        binding.rvChatAc.layoutManager = LinearLayoutManager(this)
        binding.rvChatAc.adapter = adapter

        val otherUserId = intent.getStringExtra("userId")

        startChat(otherUserId)
    }

    /**
     * Initializes the chat if a valid user ID is provided.
     * Sets up LiveData observers and handles sending messages.
     *
     * @param otherUserId The user ID of the chat partner.
     */
    private fun startChat(otherUserId: String?) {
        if (otherUserId != null) {

            chatViewModel.setOtherUserId(otherUserId)
            chatViewModel.initChat(otherUserId)

            chatViewModel.chatMessage.observe(this) { chatMessages ->
                adapter.submitList(chatMessages)
            }

            binding.fabSendAc.setOnClickListener {
                val sendChatText = binding.etMessageAc.text.toString()
                if (sendChatText.isNotBlank()) {
                    chatViewModel.sendMessage(sendChatText)
                    binding.etMessageAc.text.clear()
                    Log.d("!!!", "Sent Chat = $sendChatText")
                }
            }

        } else {
            Toast.makeText(this, "Something is wrong ", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, FriendListActivity::class.java)
            startActivity(intent)
        }
    }
}