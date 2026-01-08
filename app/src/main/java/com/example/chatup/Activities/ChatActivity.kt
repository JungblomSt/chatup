package com.example.chatup.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatup.adapters.ChatRecViewAdapter
import com.example.chatup.data.User
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
        val otherUserName = intent.getStringExtra("userName")

        val isGroup = intent.getBooleanExtra("isGroup", false)
        val groupName = intent.getStringExtra("groupName")
        val chatPartnersId = intent.getStringArrayListExtra("chatPartnersId")

        if (isGroup){
            val conversationId = intent.getStringExtra("conversationId")
            val chatPartnersIds = intent.getStringArrayListExtra("chatPartnersId") ?: emptyList()
            val chatPartnersNames = intent.getStringArrayListExtra("chatPartnersNames") ?: emptyList()
            startGroupChat(conversationId, groupName, chatPartnersIds, chatPartnersNames)
        } else{
            startPrivateChat(otherUserId,otherUserName)
        }

    }

    override fun onStart() {
        super.onStart()
        chatViewModel.setChatOpened(true)
    }

    /**
     * Initializes the chat if a valid user ID is provided.
     * Sets up LiveData observers and handles sending messages.
     *
     * @param otherUserId The user ID of the chat partner.
     */
    private fun startChat(otherUserId: String?, otherUserName : String?) {

    }

    private fun startPrivateChat(otherUserId: String?, otherUserName: String?) {
        if (otherUserId != null) {

            chatViewModel.setOtherUserId(otherUserId)
            chatViewModel.initChat(otherUserId)
            chatViewModel.setOtherUserName(otherUserName)

            binding.etMessageAc.addTextChangedListener { editText ->
                if (editText.isNullOrBlank()) {
                    chatViewModel.setTyping(false)
                } else {
                    chatViewModel.setTyping(true)
                }
            }

            chatViewModel.isTyping.observe(this) { isTyping ->
                if (isTyping) {
                    binding.tvReceiverNameAc.text = "${otherUserName} is typing..."
                } else {
                    binding.tvReceiverNameAc.text = otherUserName
                }
            }

            chatViewModel.chatMessage.observe(this) { chatMessages ->
                adapter.submitList(chatMessages)
                if (chatMessages.isNotEmpty()) {
                    binding.rvChatAc.scrollToPosition(chatMessages.size - 1) // scroll to last chatMessage
                }
            }

            chatViewModel.otherUserName.observe(this) { name ->
                adapter.setChatUsers(isGroup = false, chatPartner = name )
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

    fun startGroupChat (conversationId : String?, groupName : String?, chatPartnersId : List<String>, chatPartnersNames : List<String>) {

        binding.etMessageAc.addTextChangedListener { editText ->
            if (editText.isNullOrBlank()) {
                chatViewModel.setTyping(false)
            } else {
                chatViewModel.setTyping(true)
            }
        }

        chatViewModel.isTyping.observe(this) { isTyping ->
            if (isTyping) {
                binding.tvReceiverNameAc.text = "is typing..."
            } else {
                binding.tvReceiverNameAc.text = groupName
            }
        }

        chatViewModel.initGroupChat(convId = conversationId,
            members = chatPartnersId
        )



        val chatPartners = mutableListOf<User>()
        if (chatPartnersId.size == chatPartnersNames.size) {
            for (i in chatPartnersId.indices) {
                chatPartners.add(User(uid = chatPartnersId[i], username = chatPartnersNames[i]))
            }
        }

//        adapter.setChatUsers(isGroup = true, users = chatPartnersId.mapIndexed { index, id ->
//            User(uid = id, username = chatPartnersNames.getOrElse(index){"Unknown"})
//        })

        adapter.setChatUsers(isGroup = true, users = chatPartners )




        chatViewModel.chatMessage.observe(this) {messages ->
            adapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.rvChatAc.scrollToPosition(messages.size -1)
            }
        }

        binding.fabSendAc.setOnClickListener {
            val sendChatText = binding.etMessageAc.text.toString()
            if (sendChatText.isNotBlank()) {
                chatViewModel.sendMessage(sendChatText)
                binding.etMessageAc.text.clear()
                Log.d("!!!", "Sent GROUP Chat = $sendChatText")
            }
        }

    }

    override fun onStop() {
        chatViewModel.setTyping(false)
        chatViewModel.setChatOpened(false)
        super.onStop()
    }

}