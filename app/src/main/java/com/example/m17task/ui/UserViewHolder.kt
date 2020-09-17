package com.example.m17task.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.m17task.R
import com.example.m17task.model.User

class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val userName: TextView = view.findViewById(R.id.user_name_textView)
    private val avatarImage: ImageView = view.findViewById(R.id.avatar_image)

    fun bind(user: User?) {
        if (user == null) {
            userName.text = itemView.resources.getString(R.string.loading)
        } else {
            userName.text = user.login
            Glide.with(itemView.context).load(user.avatarUrl).into(avatarImage)
            userName.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.htmlUrl))
                it.context.startActivity(intent)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): UserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_view_item, parent, false)
            return UserViewHolder(view)
        }
    }
}
