package ru.jufy.myposh.model.interactor

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.repository.ArtworkRepository

class LibraryInteractor(val artworkRepository: ArtworkRepository) {

    fun loadData():Observable<Pair<MutableList<MarketImage>, Long>>{
        return Observable.zip(artworkRepository.getOwnedArtworks().toObservable(),
                artworkRepository.getBalance().toObservable(),
                BiFunction { artworks, balance -> return@BiFunction Pair(artworks,balance) })
    }
}