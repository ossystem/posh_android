package ru.jufy.myposh.di.module.activity

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.jufy.myposh.di.PerFragment
import ru.jufy.myposh.di.module.fragment.TabContainerModule
import ru.jufy.myposh.ui.main.TabContainerFragment

@Module
abstract class MainActivityModule {
    @PerFragment
    @ContributesAndroidInjector(modules = arrayOf(TabContainerModule::class))
    internal abstract fun tabInjector(): TabContainerFragment
}