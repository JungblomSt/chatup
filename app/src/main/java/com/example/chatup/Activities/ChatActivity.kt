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
import com.example.chatup.viewmodel.GroupChatViewModel
import com.example.chatup.viewmodel.UsersViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var usersViewModel: UsersViewModel

    private lateinit var groupChatViewModel: GroupChatViewModel

    private lateinit var chatViewModel: ChatViewModel

    private lateinit var adapter: ChatRecViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        groupChatViewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]
        usersViewModel = ViewModelProvider(this)[UsersViewModel::class.java]

        adapter = ChatRecViewAdapter()


        binding.rvChatAc.layoutManager = LinearLayoutManager(this)
        binding.rvChatAc.adapter = adapter

        val otherUserId = intent.getStringExtra("userId")
        val otherUserName = intent.getStringExtra("userName")

        val isGroup = intent.getBooleanExtra("isGroup", false)
        val groupName = intent.getStringExtra("groupName")
        Log.d("DEBUG_CHAT", "1onCreate: isGroup=$isGroup")
        Log.d("DEBUG_CHAT", "1onCreate: groupName='$groupName'")
        Log.d("DEBUG_CHAT", "1onCreate: otherUserId=$otherUserId")
        Log.d("DEBUG_CHAT", "1onCreate: otherUserName='$otherUserName'")

        Log.d(
            "CHAT_ACTIVITY",
            "onCreate: isGroup=${intent.getBooleanExtra("isGroup", false)}, conversationId=${intent.getStringExtra("conversationId")}"
        )


        if (isGroup){
            val conversationId = intent.getStringExtra("conversationId")
            val chatPartnersIds = intent.getStringArrayListExtra("chatPartnersId") ?: emptyList()
            Log.d("DEBUG_CHAT", "2onCreate: isGroup=$isGroup")
            Log.d("DEBUG_CHAT", "2onCreate: conversationId=$conversationId")
            Log.d("DEBUG_CHAT", "2onCreate: groupName='$groupName'")
            Log.d("DEBUG_CHAT", "2onCreate: otherUserId=$otherUserId")
            Log.d("DEBUG_CHAT", "2onCreate: otherUserName='$otherUserName'")
            startGroupChat(conversationId, groupName, chatPartnersIds)
        } else{
            startPrivateChat(otherUserId,otherUserName)
        }

    }


    override fun onStart() {
        super.onStart()

        val otherUserId = intent.getStringExtra("userId") ?: return
        if (intent.getBooleanExtra("isGroup", false)) {
            groupChatViewModel.setGroupChatOpened(true)
            val conversationId = intent.getStringExtra("conversationId")
            if (conversationId != null) {
                groupChatViewModel.markLastSeen(conversationId)
            }
            else{
                chatViewModel.setConversationId( otherUserId)
                chatViewModel.setOtherUserId(otherUserId)

                chatViewModel.conversationId.observe(this) { conversationId ->
                    if (conversationId != null) {
                        chatViewModel.markChatAsSeen(conversationId)
                        chatViewModel.checkDeliveredMessage(conversationId)
                    }
                }
            }
        }


    }



    /**
     * Initializes the chat if a valid user ID is provided.
     * Sets up LiveData observers and handles sending messages.
     *
     * @param otherUserId The user ID of the chat partner.
     */
    private fun startPrivateChat(otherUserId: String?, otherUserName: String?) {
        if (otherUserId == null) {
            Log.e("DEBUG_PRIVATE", "conversationId is null!")
            return
        }

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
                    binding.tvReceiverNameAc.text = "$otherUserName is typing..."
                } else {
                    binding.tvReceiverNameAc.text = otherUserName
                }
            }

            chatViewModel.otherUserName.observe(this) { name ->
                adapter.setChatUsers(isGroup = false, chatPartner = name )
            }

            chatViewModel.chatMessage.observe(this) { chatMessages ->
                Log.d("DEBUG_UI", "chatMessage observer triggered, size=${chatMessages.size}")

                adapter.submitList(chatMessages.toList())
                if (chatMessages.isNotEmpty()) {
                    binding.rvChatAc.scrollToPosition(chatMessages.size - 1) // scroll to last chatMessage
                }
            }



            binding.fabSendAc.setOnClickListener {
                val sendChatText = binding.etMessageAc.text.toString()
                if (sendChatText.isNotBlank()) {
                    chatViewModel.sendMessage(sendChatText)
                    binding.etMessageAc.text.clear()
                    Log.d("!!!", "Sent Chat = $sendChatText")
                }
            }


    }

    fun startGroupChat(conversationId: String?, groupName: String?, chatPartnersId: List<String>) {
        if (conversationId == null) {
            Log.e("DEBUG_GROUP", "conversationId is null!")
            return
        }

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

        binding.tvReceiverNameAc.text = groupName


        groupChatViewModel.initGroupChat(convId = conversationId, members = chatPartnersId)


        adapter.isGroupChat = true

        groupChatViewModel.usersMap.observe(this){ map ->
            Log.d("DEBUG_USERS_MAP!!", "Observer triggered: $map")  // <— alltid logga så du ser det
            adapter.updateUsersMap(map)
        }


        groupChatViewModel.groupChatMessage.observe(this) { messages ->
            Log.d("DEBUG_UI!!", "groupChatMessage observer triggered, size=${messages.size}")
            adapter.submitList(messages.toList())
            if (messages.isNotEmpty()) {
                binding.rvChatAc.scrollToPosition(messages.size - 1)
            }
        }


        binding.fabSendAc.setOnClickListener {
            val sendChatText = binding.etMessageAc.text.toString()
            if (sendChatText.isNotBlank()) {
                groupChatViewModel.sendGroupMessage(sendChatText)
                binding.etMessageAc.text.clear()
                Log.d("!!!", "Sent GROUP Chat = $sendChatText")
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (intent.getBooleanExtra("isGroup", false)) {
            groupChatViewModel.setGroupChatOpened(false)
        } else {
            chatViewModel.setChatOpened(false)
            chatViewModel.setTyping(false)
        }
    }

}