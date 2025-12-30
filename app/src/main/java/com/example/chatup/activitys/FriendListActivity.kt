package com.example.chatup.activitys

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.FirebaseManager
import com.example.chatup.data.User
import com.example.chatup.databinding.ActivityFriendListBinding
import com.example.chatup.viewmodel.ChatViewModel

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFriendListBinding
    private lateinit var chatViewModel : ChatViewModel

    private lateinit var adapter : ArrayAdapter<String>

    private var friendList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        initAdapter()

    }

    private fun initAdapter() : ArrayAdapter<String> {
        adapter = ArrayAdapter(
            this,
            R.layout.simple_list_item_1,
            mutableListOf<String>()
        )
        binding.lvFriendsListAfl.adapter = adapter

        return adapter
    }

    fun loadUsers () {
        chatViewModel.users.observe(this) { userList ->
            friendList.clear()
            friendList.addAll(userList)
            adapter.clear()
            adapter.addAll(userList.map { it.username })
            adapter.notifyDataSetChanged()
        }
    }
}