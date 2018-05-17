package ru.jufy.myposh.di.module



import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerApplication
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

/**
 * Created by rolea on 18.09.2017.
 */

@Module
class NavigationModule {
    private val cicerone: Cicerone<Router>

    init {
        cicerone = Cicerone.create()
    }

    @Provides
    @PerApplication
    internal fun provideRouter(): Router {
        return cicerone.router
    }

    @Provides
    @PerApplication
    internal fun provideNavigatorHolder(): NavigatorHolder {
        return cicerone.navigatorHolder
    }
}
