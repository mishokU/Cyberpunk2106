package com.example.eazyremote.di.module

import androidx.lifecycle.ViewModelProvider
import com.example.eazyremote.domain.viewmodels.DataViewModel
import com.example.eazyremote.ui.fragments.MainScreenFragment
import dagger.Module
import dagger.Provides

@Module
class InjectViewModel {

    @Provides
    fun injectDataViewModel(
        factory: ViewModelProvider.Factory,
        target: MainScreenFragment
    ) = ViewModelProvider(target, factory).get(DataViewModel::class.java)


}