package com.example.chatup.activitys

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.data.User
import com.example.chatup.databinding.ActivityFriendListBinding
import com.example.chatup.viewmodel.ChatViewModel

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding
    private lateinit var chatViewModel: ChatViewModel

    private lateinit var adapter: ArrayAdapter<String>

    private var friendList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        chatViewModel.loadAllUsers()

        initAdapter()
        loadUsers()
    }

    /*
    Todo ändra " temporärt innan vi lägger till vänner via email lägg till alla i register "

    TODO Lägga till att man lägger till vänner via ex email istället för att hämta all users ifrån db
          */
    private fun initAdapter() {
        adapter = ArrayAdapter(
            this,
            R.layout.simple_list_item_1,
            mutableListOf<String>()
        )
        binding.lvFriendsListAfl.adapter = adapter

        binding.lvFriendsListAfl.setOnItemClickListener { _, _, pos, _ ->
            val selectedUser = friendList[pos]
            chatViewModel.setOtherUserId(selectedUser.id)

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", selectedUser.id)
            intent.putExtra("userName", selectedUser.username)
            startActivity(intent)

        }


    }

    fun loadUsers() {
        chatViewModel.users.observe(this) { userList ->
            friendList.clear()
            friendList.addAll(userList)
            adapter.clear()
            adapter.addAll(userList.map { it.username })
            adapter.notifyDataSetChanged()
        }
    }
}