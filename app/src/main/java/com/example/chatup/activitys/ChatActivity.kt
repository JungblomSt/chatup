package com.example.chatup.activitys

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.R
import com.example.chatup.databinding.ActivityChatBinding
import com.example.chatup.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding

    private lateinit var chatViewModel : ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

    }
}