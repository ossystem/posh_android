package ru.jufy.myposh.di.module

import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerApplication
import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.interactor.AuthInteractor
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
    internal fun provideAuthInteractor(baseRepository: AuthRepository) = AuthInteractor(baseRepository)
}