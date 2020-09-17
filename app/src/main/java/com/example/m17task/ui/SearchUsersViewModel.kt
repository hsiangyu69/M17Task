package com.example.m17task.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.m17task.data.GithubRepository
import com.example.m17task.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchUsersViewModel(private val repository: GithubRepository) : ViewModel() {

    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<UserItem>>? = null

    val retrySearch = SingleLiveEvent<Unit>()
    val cancelSearch = SingleLiveEvent<Unit>()

    private val _errorState = MutableLiveData<ErrorState>()
    val errorState: LiveData<ErrorState> = _errorState

    fun searchUsers(queryString: String): Flow<PagingData<UserItem>> {
        val lastResult = currentSearchResult
        // avoid duplicate api call
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<UserItem>> = repository.getSearchResultStream(queryString)
            .map { pagingData -> pagingData.map { UserItem(it) } }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult

        return newResult
    }

    fun handleErrorState(state: ErrorState) {
        _errorState.value = state
    }
}

data class UserItem(val user: User)

sealed class ErrorState {
    object APIFail : ErrorState()
    object EmptyQuery : ErrorState()
    object EmptySearchResult : ErrorState()
}
