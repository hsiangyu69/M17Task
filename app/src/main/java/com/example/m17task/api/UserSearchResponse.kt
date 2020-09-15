package com.example.m17task.api

import com.example.m17task.model.User
import com.google.gson.annotations.SerializedName

/**
 * Data class to hold user responses from searchUser API calls.
 */
data class UserSearchResponse(
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<User> = emptyList(),
    val nextPage: Int? = null
)