package com.example.chatup.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.R
import com.example.chatup.Activities.ChatActivity
import com.example.chatup.Activities.MainActivity
import com.example.chatup.adapters.ConversationListAdapter
import com.example.chatup.viewmodel.ConversationListViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ConversationListFragment : Fragment(R.layout.fragment_conversation_list) {
    private lateinit var conversationListViewModel: ConversationListViewModel
    private lateinit var adapter: ConversationListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conversationListViewModel = ViewModelProvider(this)[ConversationListViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.conversationListRecycler)
        val btnUsers = view.findViewById<Button>(R.id.btnUsers)
        val btnProfile = view.findViewById<Button>(R.id.btnProfile)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = ConversationListAdapter(emptyList()) { conversation ->
            Log.d("DEBUG_CONV_LIST", "Clicked conversation ${conversation.conversationId}, type=${conversation.conversationType}, name='${conversation.name}'")

            val intent = Intent(requireContext(), ChatActivity::class.java)

            intent.putExtra("conversationId", conversation.conversationId)
            intent.putExtra("isGroup", conversation.conversationType == "group")

            if (conversation.conversationType == "group") {

                intent.putExtra("groupName", conversation.name)
                intent.putStringArrayListExtra(
                    "chatPartnersId",
                    ArrayList(conversation.users)
                )
                Log.d("DEBUG_CONV_LIST", "Group chat: groupName='${conversation.name}', members=${conversation.users}")

            } else {

                val currentUserId = Firebase.auth.currentUser?.uid
                val friendId = conversation.users.first { it != currentUserId }

                intent.putExtra("userId", friendId)
                intent.putExtra("userName", conversation.friendUsername ?: "Chat")
                Log.d("DEBUG_CONV_LIST", "Private chat with friendId=$friendId, friendUsername='${conversation.friendUsername}'")

            }

            Log.d(
                "OPEN_CHAT",
                "Open chat: id=${conversation.conversationId}, type=${conversation.conversationType}"
            )


            startActivity(intent)
        }

//        adapter = ConversationListAdapter(emptyList()) { conversation ->
//            val currentUserId = Firebase.auth.currentUser?.uid
//            val friendId = conversation.users.firstOrNull { it != currentUserId }
//
//            if (friendId != null) {
//                val intent = Intent(requireContext(), ChatActivity::class.java)
//                intent.putExtra("userId", friendId)
//                // Om friendUsername saknas kan vi kanske behöva hämta det, men vi skickar med det vi har
//                intent.putExtra("userName", conversation.friendUsername ?: "Chat")
//                startActivity(intent)
//            }
//        }

        recycler.adapter = adapter

        conversationListViewModel.conversationList.observe(viewLifecycleOwner) {
            adapter.update(it)
        }

        conversationListViewModel.getAllCurrentUserConversationLists()

    }

    override fun onResume() {
        super.onResume()
        conversationListViewModel.getAllCurrentUserConversationLists()
    }

}