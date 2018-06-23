package ru.jufy.myposh.di.module.fragment

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerChildFragment
import ru.jufy.myposh.model.interactor.FavouritesInteractor
import ru.jufy.myposh.model.repository.ArtworkRepository
import ru.jufy.myposh.model.system.ResourceManager
import ru.jufy.myposh.presentation.favourites.FavouritesMvpView
import ru.jufy.myposh.presentation.favourites.FavouritesPresenter
import ru.jufy.myposh.presentation.global.ErrorHandler

@Module
object FavouritesModule {
    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun favouritesPresenter(favouritesInteractor: FavouritesInteractor, resourceManager: ResourceManager, errorHandler: ErrorHandler)
            : FavouritesPresenter<FavouritesMvpView> = FavouritesPresenter(favouritesInteractor, resourceManager, errorHandler)

    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun favouritesInteractor(artworkRepository: ArtworkRepository)
            : FavouritesInteractor = FavouritesInteractor(artworkRepository)
}