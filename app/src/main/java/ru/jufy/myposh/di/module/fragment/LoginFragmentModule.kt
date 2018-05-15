package ru.jufy.myposh.di.module.fragment

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerFragment
import ru.jufy.myposh.model.interactor.AuthInteractor
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.presentation.auth.AuthPresenter
import ru.jufy.myposh.presentation.auth.AuthMvpView

@Module
object LoginFragmentModule {
    @JvmStatic
    @Provides
    @PerFragment
    internal fun likesPresenter(newsInteractor: AuthInteractor, errorHandler: ErrorHandler)
            :AuthPresenter<AuthMvpView> = AuthPresenter(newsInteractor, errorHandler)
}
