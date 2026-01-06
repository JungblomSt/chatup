package com.example.chatup.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.R
import com.example.chatup.fragments.ConversationListFragment
import com.example.chatup.fragments.UsersFragment
import com.example.chatup.viewmodel.ChatViewModel

class StartMenuActivity : AppCompatActivity(){

    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_menu_activity)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        showConversations()

        showUsers()
    }

    override fun onStart() {
        super.onStart()
        chatViewModel.checkDeliveredMessage()

    }

    fun showConversations() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.conversationListContainer, ConversationListFragment())
            .commit()
    }

    fun showUsers() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.usersContainer, UsersFragment())
            .commit()
    }
}