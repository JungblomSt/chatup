package com.example.chatup

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.fragments.ConversationListFragment
import com.example.chatup.fragments.UsersFragment
import com.example.chatup.viewmodel.AuthViewModel
import com.example.chatup.viewmodel.ChatViewModel
import com.google.android.material.navigation.NavigationView
import android.widget.TextView
import android.content.Intent
import androidx.core.view.GravityCompat
import com.example.chatup.Activities.SettingsActivity
import com.example.chatup.Activities.LoginActivity

class StartMenuActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    private lateinit var auth: AuthViewModel
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_menu_activity)

        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = Color.WHITE

        val headerViewHamburgerMenu = navigationView.getHeaderView(0)
        val tvMail = headerViewHamburgerMenu.findViewById<TextView>(R.id.tv_email)
        tvMail.text = auth.getCurrentUser()?.email ?: "Ingen e-post"

        showConversations()
        showUsers()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_chats, R.id.menu_users -> {
                    showConversations()
                    showUsers()
                }

                R.id.menu_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.menu_logout -> {
                    auth.signOut()
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun showConversations() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.conversationListContainer, ConversationListFragment())
            .commit()
        findViewById<FrameLayout>(R.id.conversationListContainer).visibility = View.VISIBLE
    }

    private fun showUsers() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.usersContainer, UsersFragment())
            .commit()
        findViewById<FrameLayout>(R.id.usersContainer).visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        chatViewModel.checkDeliveredMessage()
    }
}
