package ru.jufy.myposh.di.module


import com.jufy.mgtshr.ui.subnavigation.LocalCiceroneHolder
import dagger.Module
import dagger.Provides
import ru.jufy.myposh.di.PerApplication

/**
 * Created by rolea on 18.09.2017.
 */

@Module
object LocalNavigationModule {
    @JvmStatic
    @Provides
    @PerApplication
    internal fun provideLocalNavigationHolder(): LocalCiceroneHolder {
        return LocalCiceroneHolder()
    }
}
