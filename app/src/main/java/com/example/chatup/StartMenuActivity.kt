package com.example.chatup

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.fragments.ConversationListFragment
import com.example.chatup.fragments.UsersFragment
import com.example.chatup.viewmodel.AuthViewModel

class StartMenuActivity : AppCompatActivity(){

    private lateinit var auth: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_menu_activity)

        auth = ViewModelProvider(this)[AuthViewModel::class.java]

        val tvEmail = findViewById<TextView>(R.id.tv_email)
        val btnLogout = findViewById<Button>(R.id.btn_logout)

        tvEmail.text = auth.getCurrentUser()?.email

        btnLogout.setOnClickListener {
            auth.signOut()
            finish()
        }

        showConversations()

        showUsers()
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