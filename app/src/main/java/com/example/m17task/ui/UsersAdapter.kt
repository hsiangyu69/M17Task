package com.example.m17task.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter : PagingDataAdapter<UserItem, RecyclerView.ViewHolder>(USER_DIFFUTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userItem = getItem(position)
        userItem?.let {
            (holder as UserViewHolder).bind(user = it.user)
        }
    }

    companion object {
        private val USER_DIFFUTIL = object : DiffUtil.ItemCallback<UserItem>() {
            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.user.id == newItem.user.id
            }

            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean =
                oldItem.user.login == newItem.user.login
        }
    }
}