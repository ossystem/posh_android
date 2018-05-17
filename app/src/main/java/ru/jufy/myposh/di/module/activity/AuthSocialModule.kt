package ru.jufy.myposh.di.module.activity

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerActivity
import ru.jufy.myposh.model.interactor.AuthInteractor
import ru.jufy.myposh.presentation.auth.social.AuthSocialMvpView
import ru.jufy.myposh.presentation.auth.social.AuthSocialPresenter
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.terrakok.cicerone.Router

@Module
abstract class AuthSocialModule {
    @Module
    companion object {
        @JvmStatic
        @Provides
        @PerActivity
        fun providePresenter(interactor: AuthInteractor, errorHandler: ErrorHandler,
                             router: Router) = AuthSocialPresenter<AuthSocialMvpView>(interactor, errorHandler, router)
    }
}