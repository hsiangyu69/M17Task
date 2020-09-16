package com.example.m17task.model

import com.google.gson.annotations.SerializedName

data class User(
    @field:SerializedName("login") val login: String,
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("avatar_url") val avatarUrl: String,
    @field:SerializedName("html_url") val htmlUrl: String
)