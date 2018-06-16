package ru.jufy.myposh.di.module.fragment

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerChildFragment
import ru.jufy.myposh.model.interactor.DetailArtworkInteractor
import ru.jufy.myposh.model.interactor.LikeArtworkInteractor
import ru.jufy.myposh.model.repository.ArtworkRepository
import ru.jufy.myposh.model.system.ResourceManager
import ru.jufy.myposh.presentation.artwork.detail.DetailArtworkMvpView
import ru.jufy.myposh.presentation.artwork.detail.DetailArtworkPresenter
import ru.jufy.myposh.presentation.global.ErrorHandler

@Module
object DetailArtworkModule {
    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun detailArtworkPresenter(storeInteractor: DetailArtworkInteractor, likeArtworkInteractor: LikeArtworkInteractor,
                                        resourceManager: ResourceManager, errorHandler: ErrorHandler)
            : DetailArtworkPresenter<DetailArtworkMvpView> = DetailArtworkPresenter(storeInteractor,likeArtworkInteractor, resourceManager, errorHandler)

    @JvmStatic
    @Provides
    @PerChildFragment
    internal fun detailArtworkInteractor(artworkRepository: ArtworkRepository)
            : DetailArtworkInteractor = DetailArtworkInteractor(artworkRepository)
}