package ru.jufy.myposh.di.module

import android.app.Application
import ru.jufy.myposh.di.PerApplication
import dagger.Module
import dagger.Provides
import ru.jufy.myposh.models.storage.UserPreferences

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

}
