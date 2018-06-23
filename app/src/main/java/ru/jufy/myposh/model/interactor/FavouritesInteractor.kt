package ru.jufy.myposh.model.interactor

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.repository.ArtworkRepository


class FavouritesInteractor(val artworkRepository: ArtworkRepository) {

    fun getFavourites() = artworkRepository.getFavouritesArtworks()
            .toObservable()
            .flatMapIterable { x->x }
            .map {
                it as Any }
            .toList()
            .toObservable()

    fun deleteFavourites(images:List<MarketImage>):Observable<List<ImageDeletedResponse>>{
        val requestsToDeletes = images.map { getFavouriteObservable(it.id, !it.isFavorite) }

        return Observable.zip(requestsToDeletes)
                            { return@zip it.map { it as ImageDeletedResponse } }
                .subscribeOn(Schedulers.io())
    }

    private fun getFavouriteObservable(id:String, isLiked:Boolean):Observable<ImageDeletedResponse>{
        return artworkRepository.toggleLike(id, isLiked)
                .toObservable()
                .map { return@map ImageDeletedResponse(id, true)}
                        .onErrorResumeNext { _: Throwable ->
                                Observable.just(ImageDeletedResponse(id, false))}
    }

    class ImageDeletedResponse(val id:String, val isDeleted:Boolean)

}