package ru.jufy.myposh.di.module.fragment


import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.jufy.myposh.di.PerChildFragment
import ru.jufy.myposh.ui.artwork.detail.ImageFragment
import ru.jufy.myposh.ui.fragments.FavoritesFragment
import ru.jufy.myposh.ui.fragments.LibraryFragment
import ru.jufy.myposh.ui.store.MarketFragment

/**
 * Created by rolea on 04.03.2018.
 */
@Module
public abstract class TabContainerModule {
    @PerChildFragment
    @ContributesAndroidInjector(modules = [(StoreModule::class)])
    internal abstract fun storeInjector(): MarketFragment

    @PerChildFragment
    @ContributesAndroidInjector(modules = [(BaseModule::class)])
    internal abstract fun favouritesInjector(): FavoritesFragment

    @PerChildFragment
    @ContributesAndroidInjector(modules = [(BaseModule::class)])
    internal abstract fun libraryInjector(): LibraryFragment

    @PerChildFragment
    @ContributesAndroidInjector(modules = [(DetailArtworkModule::class)])
    internal abstract fun detailArtworkInjector(): ImageFragment
}