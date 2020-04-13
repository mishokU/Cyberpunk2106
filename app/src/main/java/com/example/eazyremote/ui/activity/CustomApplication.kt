package com.example.eazyremote.ui.activity

import android.app.Activity
import android.app.Application
import com.example.eazyremote.di.utils.AppInjector
import com.example.eazyremote.di.component.AppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class CustomApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    lateinit var component : AppComponent

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)
    }

    override fun activityInjector() = activityInjector
}