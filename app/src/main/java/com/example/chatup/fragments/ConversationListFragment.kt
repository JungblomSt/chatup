package com.example.chatup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.R
import com.example.chatup.adapters.ConversationListAdapter
import com.example.chatup.viewmodel.ConversationListViewModel

class ConversationListFragment : Fragment(R.layout.fragment_conversation_list) {
    private lateinit var conversationListViewModel: ConversationListViewModel
    private lateinit var adapter: ConversationListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conversationListViewModel = ViewModelProvider(this)[ConversationListViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.conversationListRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = ConversationListAdapter(emptyList())
        recycler.adapter = adapter

        conversationListViewModel.conversationList.observe(viewLifecycleOwner) {
            adapter.update(it)
        }

        conversationListViewModel.getAllCurrentUserConversationLists()
    }
}