package com.example.chatup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.databinding.ItemConversationListLayoutBinding
import com.example.chatup.fragments.UsersFragment

class StartMenuActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_menu_activity)

        //setContentView(binding.root)

        showUsers()
    }

    fun showConversations() {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainer, ConversationsFragment())
//            .commit()
    }

    fun showUsers() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, UsersFragment())
            .commit()
    }
}