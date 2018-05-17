package ru.jufy.myposh.di.component

import android.app.Application
import android.content.Context
import ru.jufy.myposh.di.ApplicationContext
import ru.jufy.myposh.di.PerApplication
import dagger.Component
import dagger.android.AndroidInjector
import retrofit2.Retrofit
import ru.jufy.myposh.MyPoshApplication
import ru.jufy.myposh.di.module.*
import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.storage.UserPreferences

/**
 * Created by rolea on 4/22/2017.
 */

@PerApplication
@Component(modules = arrayOf(AppModule::class, NetModule::class, DataBaseModule::class, RepositoryModule::class,
        NavigationModule::class, LocalNavigationModule::class))
interface ApplicationComponent : AndroidInjector<MyPoshApplication> {

    @ApplicationContext
    fun context(): Context

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MyPoshApplication>()

    fun application(): Application
    fun retrofit(): Retrofit
    fun userPreferences(): UserPreferences
    fun apiService(): ApiService
/*    fun router(): Router
    fun localCiceroneHolder(): LocalCiceroneHolder*/

}
