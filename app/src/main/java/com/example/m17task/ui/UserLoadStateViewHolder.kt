package com.example.m17task.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.m17task.R

class UserLoadStateViewHolder(
    view: View,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(view) {
    private val errorMessage = view.findViewById<TextView>(R.id.error_msg)
    private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
    private val retryButton = view.findViewById<Button>(R.id.retry_button)

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            errorMessage.text = loadState.error.localizedMessage
        }
        errorMessage.isVisible = loadState !is LoadState.Loading
        progressBar.isVisible = loadState is LoadState.Loading
        retryButton.isVisible = loadState !is LoadState.Loading
        retryButton.setOnClickListener {
            retry.invoke()
        }
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): UserLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_load_state_footer_view_item, parent, false)
            return UserLoadStateViewHolder(view, retry)
        }
    }
}