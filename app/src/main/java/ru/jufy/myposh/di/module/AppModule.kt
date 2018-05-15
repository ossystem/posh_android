package ru.jufy.myposh.di.module

import android.app.Application
import android.content.Context

import ru.jufy.myposh.di.ApplicationContext
import ru.jufy.myposh.di.PerActivity
import ru.jufy.myposh.di.PerApplication

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.jufy.myposh.MyPoshApplication
import ru.jufy.myposh.di.module.activity.LoginActivityModule
import ru.jufy.myposh.ui.activities.LaunchActivity

/**
 * Created by rolea on 14.09.2017.
 * Provide Activity here
 * Example: @PerActivity
 * @ContributesAndroidInjector(modules = MainActivityModule.class)
 * abstract MainActivity mainActivityInjector();
 */

@Module(includes = arrayOf(AndroidSupportInjectionModule::class))
abstract class AppModule {

    @Binds
    @ApplicationContext
    internal abstract fun provideContext(application: Application): Context

    @Binds
    @PerApplication
    internal abstract// Singleton annotation isn't necessary (in this case since Application instance is unique)
            // but is here for convention.
    fun application(app: MyPoshApplication): Application

    @PerActivity
    @ContributesAndroidInjector(modules = arrayOf(LoginActivityModule::class))
    internal abstract fun loginActivityInjector(): LaunchActivity

}
