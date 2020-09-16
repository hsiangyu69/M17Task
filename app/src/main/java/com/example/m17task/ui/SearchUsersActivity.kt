package com.example.m17task.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.m17task.R
import dagger.Lazy
import kotlinx.android.synthetic.main.activity_search_users.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchUsersActivity : AppCompatActivity() {

    @Inject
    lateinit var searchUsersViewModelCreator: Lazy<SearchUsersViewModel>

    private lateinit var searchUsersViewModel: SearchUsersViewModel

    private val usersAdapter = UsersAdapter()

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)
        // get view model
        searchUsersViewModel = getViewModel(searchUsersViewModelCreator)

        initSearchUsersList()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: ""
        search(query)
        initSearch(query)
        retry_button.setOnClickListener { usersAdapter.retry() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, search_user.text.trim().toString())
    }

    private fun initSearchUsersList() {
        // assign adapter
        search_result_recyclerView.adapter = usersAdapter

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        search_result_recyclerView.addItemDecoration(decoration)

        usersAdapter.addLoadStateListener { loadState ->
            // show the search result if loading succeeds
            search_result_recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            // show progress bar during loading or refresh
            progress_bar.isVisible = loadState.source.refresh is LoadState.Loading
            // show retry button when loading fail
            retry_button.isVisible = loadState.source.refresh is LoadState.Error
            error_message.isVisible = loadState.source.refresh is LoadState.Error

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error

            errorState?.let {
                Toast.makeText(
                    this,
                    "Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initSearch(query: String) {
        search_user.setText(query)

        search_user.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateSearchUsersListFromInput()
                true
            } else {
                false
            }
        }

        search_user.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateSearchUsersListFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateSearchUsersListFromInput() {
        search_user.text.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }

    private fun search(query: String) {
        if (query.isEmpty()) {
            empty_query_message.isVisible = true
            return
        } else {
            empty_query_message.isVisible = false
        }
        // make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            searchUsersViewModel.searchUsers(query).collectLatest {
                usersAdapter.submitData(it)
            }
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
    }
}