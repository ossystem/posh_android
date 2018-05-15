package ru.jufy.myposh.di.module

import android.app.Application
import ru.jufy.myposh.di.PerApplication
import dagger.Module
import dagger.Provides
import ru.jufy.myposh.model.storage.UserPreferences
import ru.jufy.myposh.model.system.ResourceManager

/**
 * Created by rolea on 17.09.2017.
 */

@Module
class DataBaseModule {
    @Provides
    @PerApplication
    internal fun provideUserPreferences(application: Application): UserPreferences {
        return UserPreferences(application)
    }

    @Provides
    @PerApplication
    internal fun provideResourceManager(application: Application):ResourceManager {
        return ResourceManager(application)
    }
}
