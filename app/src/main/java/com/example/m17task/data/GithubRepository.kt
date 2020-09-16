package com.example.m17task.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.m17task.api.GithubService
import com.example.m17task.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that works with data sources.
 */
class GithubRepository(private val service: GithubService) {

    /**
     * Search users whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    fun getSearchResultStream(query: String): Flow<PagingData<User>> {
        return Pager(config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = true),
            pagingSourceFactory = { GithubPagingSource(service, query) }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}