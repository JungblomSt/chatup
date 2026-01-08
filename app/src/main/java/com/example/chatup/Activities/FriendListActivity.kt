package com.example.chatup.Activities

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatup.data.User
import com.example.chatup.databinding.ActivityFriendListBinding
import com.example.chatup.viewmodel.ChatViewModel
import com.example.chatup.viewmodel.GroupChatViewModel
import com.example.chatup.viewmodel.UsersViewModel
import kotlin.compareTo

class FriendListActivity : AppCompatActivity() {

    private lateinit var groupChatViewModel: GroupChatViewModel
    private lateinit var usersViewModel: UsersViewModel
    private lateinit var binding: ActivityFriendListBinding
    private lateinit var chatViewModel: ChatViewModel

    private lateinit var adapter: ArrayAdapter<String>

    private var friendList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        usersViewModel = ViewModelProvider(this)[UsersViewModel::class.java]

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
            R.layout.simple_list_item_multiple_choice,
            friendList.map { it.username }
        )
        binding.lvFriendsListAfl.adapter = adapter

        val selectedUser = mutableListOf<User>()

        binding.lvFriendsListAfl.setOnItemClickListener { _, view, pos, _ ->
            val user = friendList[pos]
            val checkedView = view as CheckedTextView

//            chatViewModel.setOtherUserId(selectedUser.uid)

            if (checkedView.isChecked) {
                selectedUser.add(user)
            } else {
                selectedUser.remove(user)

            }


        }
        val groupName = binding.etSearchFriendAfl.text.toString()

        binding.fabStartGroupChatAfl.setOnClickListener {

            if (binding.etSearchFriendAfl.text.isBlank()) {
                Toast.makeText(this, "Choose a group name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            if (selectedUser.size < 2) {
                Toast.makeText(this, "Choose 2 users for group chat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedUserIds = selectedUser.map { it.uid }
            val selectedUsersName = selectedUser.map { it.username }



            usersViewModel.createGroup(groupName = groupName, selectedUserIds) { conversationId ->
                Log.d("DEBUG_GROUP", "Created group conversationId = $conversationId")


                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("conversationId", conversationId)
                intent.putExtra("isGroup", true)
                intent.putExtra("groupName", groupName)
                intent.putExtra("chatPartnersId", ArrayList(selectedUserIds))
                intent.putStringArrayListExtra("chatPartnersNames", ArrayList(selectedUsersName))

                startActivity(intent)
            }

        }


    }

    fun loadUsers() {
        usersViewModel.getAllUsers()

        usersViewModel.users.observe(this) { userList ->
            friendList.clear()
            friendList.addAll(userList)
            adapter.clear()
            adapter.addAll(userList.map { it.username })
            adapter.notifyDataSetChanged()
        }

//        chatViewModel.users.observe(this) { userList ->
//            friendList.clear()
//            friendList.addAll(userList)
//            adapter.clear()
//            adapter.addAll(userList.map { it.username })
//            adapter.notifyDataSetChanged()
//        }
    }
}