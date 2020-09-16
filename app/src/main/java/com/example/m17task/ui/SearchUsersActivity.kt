package com.example.m17task.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.m17task.R
import dagger.Lazy
import javax.inject.Inject

class SearchUsersActivity : AppCompatActivity() {

    @Inject
    lateinit var searchUsersViewModelCreator: Lazy<SearchUsersViewModel>

    private lateinit var searchUsersViewModel: SearchUsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)
        searchUsersViewModel = getViewModel(searchUsersViewModelCreator)
    }
}