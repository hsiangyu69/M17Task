package com.example.m17task.di

import com.example.m17task.ui.SearchUsersActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(searchUsersActivity: SearchUsersActivity)
}