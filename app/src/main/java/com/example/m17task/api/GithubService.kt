package com.example.m17task.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Github API communication setup via Retrofit.
 */
interface GithubService {
    /**
     * Get users
     */
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): UserSearchResponse

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}