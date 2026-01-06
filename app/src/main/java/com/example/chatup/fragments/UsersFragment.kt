package com.example.chatup.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.R
import com.example.chatup.Activities.ChatActivity
import com.example.chatup.adapters.UserAdapter
import com.example.chatup.viewmodel.UsersViewModel

class UsersFragment : Fragment(R.layout.fragment_user) {

    private lateinit var userViewModel: UsersViewModel
    private lateinit var adapter: UserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this)[UsersViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.usersRecycler)
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = UserAdapter(emptyList()) { user ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("userId", user.uid)
            intent.putExtra("userName", user.username)
            startActivity(intent)
        }
        recycler.adapter = adapter

        userViewModel.users.observe(viewLifecycleOwner) {
            adapter.update(it)
        }

        userViewModel.getAllUsers()

        // Lyssna på sökningar
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                userViewModel.searchUsers(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                userViewModel.searchUsers(newText ?: "")
                return true
            }
        })
    }
}
