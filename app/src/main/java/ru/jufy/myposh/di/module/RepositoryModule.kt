package ru.jufy.myposh.di.module

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerApplication
import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.interactor.AuthInteractor
import ru.jufy.myposh.model.interactor.BaseInteractor
import ru.jufy.myposh.model.interactor.LikeArtworkInteractor
import ru.jufy.myposh.model.interactor.StoreInteractor
import ru.jufy.myposh.model.repository.ArtistRepository
import ru.jufy.myposh.model.repository.ArtworkRepository
import ru.jufy.myposh.model.repository.AuthRepository
import ru.jufy.myposh.model.repository.BaseRepository
import ru.jufy.myposh.model.storage.UserPreferences

@Module
class RepositoryModule {
    @Provides
    @PerApplication
    internal fun provideBaseRepository(preferences: UserPreferences, apiService: ApiService): BaseRepository {
        return BaseRepository(apiService, preferences)
    }

    @Provides
    @PerApplication
    internal fun provideUserRepository(preferences: UserPreferences, apiService: ApiService): AuthRepository {
        return AuthRepository(apiService, preferences)
    }

    @Provides
    @PerApplication
    internal fun provideArtworkRepository(preferences: UserPreferences, apiService: ApiService): ArtworkRepository {
        return ArtworkRepository(apiService, preferences)
    }

    @Provides
    @PerApplication
    internal fun provideArtistRepository(preferences: UserPreferences, apiService: ApiService): ArtistRepository {
        return ArtistRepository(apiService, preferences)
    }

    @Provides
    @PerApplication
    internal fun provideBaseInteractor(baseRepository: BaseRepository) = BaseInteractor(baseRepository)

    @Provides
    @PerApplication
    internal fun provideAuthInteractor(baseRepository: AuthRepository) = AuthInteractor(baseRepository)

    @Provides
    @PerApplication
    internal fun provideStoreInteractor(artworkRepository: ArtworkRepository, artistRepository: ArtistRepository)
            = StoreInteractor(artworkRepository, artistRepository)

    @Provides
    @PerApplication
    internal fun provideLikeInteractor(artworkRepository: ArtworkRepository) = LikeArtworkInteractor(artworkRepository)
}