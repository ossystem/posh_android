package ru.jufy.myposh.di.module.fragment

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerChildFragment
import ru.jufy.myposh.model.interactor.LibraryInteractor
import ru.jufy.myposh.model.repository.ArtworkRepository
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.presentation.library.LibraryMvpView
import ru.jufy.myposh.presentation.library.LibraryPresenter

@Module
object LibraryModule {
    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun libraryPresenter(storeInteractor: LibraryInteractor, errorHandler: ErrorHandler)
            : LibraryPresenter<LibraryMvpView> = LibraryPresenter(storeInteractor, errorHandler)

    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun libraryInteractor(artworkRepository: ArtworkRepository)
            : LibraryInteractor = LibraryInteractor(artworkRepository)
}