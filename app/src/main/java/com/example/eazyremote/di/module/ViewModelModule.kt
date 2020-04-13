package com.example.eazyremote.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eazyremote.di.component.ViewModelKey
import com.example.eazyremote.domain.viewmodels.DataViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DataViewModel::class)
    abstract fun provideDataViewModel(viewModel: DataViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}