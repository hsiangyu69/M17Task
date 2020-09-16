package com.example.m17task.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.m17task.MainApp
import com.example.m17task.di.AppComponent
import dagger.Lazy

fun AppCompatActivity.appComponent(): AppComponent = (applicationContext as MainApp).getAppComponent()
/**
 * Like [AppCompatActivity.viewModelProvider] for AppCompatActivity() that want a [ViewModel] scoped to the Activity.
 */
inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(creator: Lazy<T>? = null): T {
    return if (creator == null)
        ViewModelProvider(this).get(T::class.java)
    else
        ViewModelProvider(this, BaseViewModelFactory { creator.get() }).get(T::class.java)
}