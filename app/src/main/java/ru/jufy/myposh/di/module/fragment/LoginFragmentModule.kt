package ru.jufy.myposh.di.module.fragment

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerFragment
import ru.jufy.myposh.model.interactor.AuthInteractor
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.presentation.auth.phone.AuthPresenter
import ru.jufy.myposh.presentation.auth.phone.AuthMvpView
import ru.terrakok.cicerone.Router

@Module
object LoginFragmentModule {
    @JvmStatic
    @Provides
    @PerFragment
    internal fun authPresenter(newsInteractor: AuthInteractor, errorHandler: ErrorHandler, router: Router)
            : AuthPresenter<AuthMvpView> = AuthPresenter(newsInteractor, errorHandler, router)
}
