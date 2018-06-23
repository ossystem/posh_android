package ru.jufy.myposh.di.module.activity

import ru.jufy.myposh.di.PerFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import ru.jufy.myposh.di.PerActivity
import ru.jufy.myposh.di.module.fragment.LoginFragmentModule
import ru.jufy.myposh.model.interactor.BaseInteractor
import ru.jufy.myposh.presentation.launch.LaunchMvpView
import ru.jufy.myposh.presentation.launch.LaunchPresenter
import ru.jufy.myposh.ui.auth.AuthPhoneFragment
import ru.terrakok.cicerone.Router

/**
 * Created by rolea on 01.03.2018.
 */
@Module
abstract class LoginActivityModule {
    @PerFragment
    @ContributesAndroidInjector(modules = arrayOf(LoginFragmentModule::class))
    internal abstract fun authInjector(): AuthPhoneFragment

    @Module
    companion object {
        @JvmStatic
        @Provides
        @PerActivity
        fun providePresenter(interactor:BaseInteractor, router: Router) = LaunchPresenter<LaunchMvpView>(interactor, router)
    }
}