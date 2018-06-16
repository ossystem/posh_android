package ru.jufy.myposh.di.module.fragment

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerChildFragment
import ru.jufy.myposh.model.interactor.StoreInteractor
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.presentation.store.StoreMvpView
import ru.jufy.myposh.presentation.store.StorePresenter

@Module
object StoreModule {
    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun storePresenter(storeInteractor: StoreInteractor, errorHandler: ErrorHandler)
            : StorePresenter<StoreMvpView> = StorePresenter(storeInteractor, errorHandler)
}