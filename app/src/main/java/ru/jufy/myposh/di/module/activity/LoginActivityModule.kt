package ru.jufy.myposh.di.module.activity

import ru.jufy.myposh.di.PerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.jufy.myposh.di.module.fragment.LoginFragmentModule
import ru.jufy.myposh.ui.fragments.LoginFragment

/**
 * Created by rolea on 01.03.2018.
 */
@Module
abstract class LoginActivityModule {
    @PerFragment
    @ContributesAndroidInjector(modules = arrayOf(LoginFragmentModule::class))
    internal abstract fun tabInjector(): LoginFragment
}