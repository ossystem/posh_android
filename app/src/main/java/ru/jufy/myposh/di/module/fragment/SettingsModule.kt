package ru.jufy.myposh.di.module.fragment

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerChildFragment
import ru.jufy.myposh.model.interactor.FavouritesInteractor
import ru.jufy.myposh.model.interactor.SettingsInteractor
import ru.jufy.myposh.model.repository.ArtworkRepository
import ru.jufy.myposh.model.repository.BaseRepository
import ru.jufy.myposh.model.system.ResourceManager
import ru.jufy.myposh.presentation.favourites.FavouritesMvpView
import ru.jufy.myposh.presentation.favourites.FavouritesPresenter
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.presentation.settings.SettingsMvpView
import ru.jufy.myposh.presentation.settings.SettingsPresenter

@Module
object SettingsModule {
    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun settingsPresenter(favouritesInteractor: SettingsInteractor, resourceManager: ResourceManager, errorHandler: ErrorHandler)
            : SettingsPresenter<SettingsMvpView> = SettingsPresenter(favouritesInteractor, resourceManager, errorHandler)

    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun settingsInteractor(artworkRepository: BaseRepository)
            : SettingsInteractor = SettingsInteractor(artworkRepository)
}