package com.example.m17task.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
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

        initView()
        observeViewAction()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: ""
        initSearch(query)
        search(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, search_user.text.trim().toString())
    }

    private fun initView() {
        retry_button.setOnClickListener {
            searchUsersViewModel.retrySearch.call()
        }
        input_layout.setEndIconOnClickListener {
            searchUsersViewModel.cancelSearch.call()
        }
        initSearchUsersList()
    }

    private fun observeViewAction() {
        searchUsersViewModel.retrySearch.observe(this, Observer {
            usersAdapter.retry()
        })
        searchUsersViewModel.cancelSearch.observe(this, Observer {
            search_user.text.clear()
            lifecycleScope.launch {
                usersAdapter.submitData(PagingData.empty())
                error_message.isVisible = true
                searchUsersViewModel.handleErrorState(ErrorState.EmptyQuery)
            }
        })

        searchUsersViewModel.errorState.observe(this, Observer { state ->
            var errorMessage = when (state) {
                is ErrorState.APIFail -> getString(R.string.network_error)
                ErrorState.EmptyQuery -> getString(R.string.try_to_search_something)
                ErrorState.EmptySearchResult -> getString(R.string.no_search_result)
            }
            error_message.text = errorMessage
        })
    }

    private fun initSearchUsersList() {
        // assign adapter
//        search_result_recyclerView.adapter = usersAdapter
        search_result_recyclerView.adapter =
            usersAdapter.withLoadStateFooter(footer = UserLoadStateAdapter { usersAdapter.retry() })

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        search_result_recyclerView.addItemDecoration(decoration)

        usersAdapter.addLoadStateListener { loadState ->
            // show progress bar during loading or refresh
            progress_bar.isVisible = loadState.source.refresh is LoadState.Loading
            // show the search result if loading succeeds
            search_result_recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            // show retry button when loading fail
            retry_button.isVisible = loadState.source.refresh is LoadState.Error
            // empty result
            val isEmptyResult =
                loadState.source.refresh is LoadState.NotLoading && usersAdapter.itemCount < 1
            // show error message when api fail or empty result
            error_message.isVisible = loadState.source.refresh is LoadState.Error || isEmptyResult
            if (error_message.isVisible) {
                when {
                    loadState.source.refresh is LoadState.Error -> searchUsersViewModel.handleErrorState(
                        ErrorState.APIFail
                    )
                    isEmptyResult -> searchUsersViewModel.handleErrorState(ErrorState.EmptySearchResult)
                }
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
        error_message.isVisible = query.isEmpty()
        if (query.isEmpty()) {
            searchUsersViewModel.handleErrorState(ErrorState.EmptyQuery)
            return
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