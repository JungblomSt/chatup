package com.example.chatup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.R
import com.example.chatup.adapters.UserAdapter
import com.example.chatup.viewmodel.UsersViewModel

class UsersFragment : Fragment(R.layout.fragment_user) {

    private lateinit var userViewModel: UsersViewModel
    private lateinit var adapter: UserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this)[UsersViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.usersRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = UserAdapter(emptyList())
        recycler.adapter = adapter

        userViewModel.users.observe(viewLifecycleOwner) {
            adapter.update(it)
        }

        userViewModel.getAllUsers()
    }
}
