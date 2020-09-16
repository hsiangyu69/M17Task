package com.example.m17task.di

import com.example.m17task.api.GithubService
import com.example.m17task.data.GithubRepository
import com.example.m17task.ui.SearchUsersViewModel
import dagger.Module
import dagger.Provides

@Module
class SearchUsersModule {

    @Provides
    fun provideGithubRepository(githubService: GithubService): GithubRepository {
        return GithubRepository(githubService)
    }

    @Provides
    fun provideSearchUsersViewModel(repository: GithubRepository): SearchUsersViewModel {
        return SearchUsersViewModel(repository)
    }
}