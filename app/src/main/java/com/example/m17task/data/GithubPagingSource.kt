package com.example.m17task.data

import androidx.paging.PagingSource
import com.example.m17task.api.GithubService
import com.example.m17task.model.User
import retrofit2.HttpException
import java.io.IOException

class GithubPagingSource(
    private val service: GithubService,
    private val query: String
) : PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX
        val apiQuery = query
        return try {
            val response = service.searchUsers(apiQuery, position, params.loadSize)
            val users = response.items
            LoadResult.Page(
                data = users,
                prevKey = if (position == GITHUB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (users.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        // GitHub page API is 1 based: https://developer.github.com/v3/#pagination
        private const val GITHUB_STARTING_PAGE_INDEX = 1
    }
}