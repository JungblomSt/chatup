package com.example.chatup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatup.data.User
import com.example.chatup.databinding.ItemUserBinding


class UserAdapter(private var users: List<User>) :
    RecyclerView.Adapter<UserAdapter.UsersViewHolder>() {

    inner class UsersViewHolder (val binding : ItemUserBinding) : RecyclerView.ViewHolder (binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvUsername.text = user.username
    }

    override fun getItemCount() = users.size

    fun update(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
