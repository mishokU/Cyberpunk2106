package com.example.eazyremote.di.component

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.eazyremote.di.module.ActivitiesModule
import com.example.eazyremote.di.module.AppModule
import com.example.eazyremote.ui.activity.CustomApplication
import dagger.BindsInstance
import dagger.Component
import dagger.MapKey
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton
import kotlin.reflect.KClass


/* Key used to associate ViewModel types with providers */
@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ViewModelKey(
    val value: KClass<out ViewModel>
)

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivitiesModule::class,
    AppModule::class
])

interface AppComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(application: CustomApplication)
}