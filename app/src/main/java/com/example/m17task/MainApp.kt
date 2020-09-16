package com.example.m17task

import android.app.Application
import com.example.m17task.di.AppComponent
import com.example.m17task.di.DaggerAppComponent

class MainApp : Application() {

    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun getAppComponent(): AppComponent {
        if (appComponent == null) {
            synchronized(this) {
                if (appComponent == null) {
                    appComponent = DaggerAppComponent.builder().build()
                }
            }
        }

        return requireNotNull(appComponent)
    }
}