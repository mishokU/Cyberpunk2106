package com.example.eazyremote.di.module

import com.example.eazyremote.ui.fragments.CarCharacteristicsFragment
import com.example.eazyremote.ui.fragments.CarSettingsFragment
import com.example.eazyremote.ui.fragments.CreateConnectionFragment
import com.example.eazyremote.ui.fragments.MainScreenFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMainScreenFragment(): MainScreenFragment

    @ContributesAndroidInjector
    abstract fun contributeCarCharacteristicsFragment(): CarCharacteristicsFragment

    @ContributesAndroidInjector
    abstract fun contributeCarSettingsFragment() : CarSettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeConnectionFragment(): CreateConnectionFragment
}