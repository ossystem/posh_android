package ru.jufy.myposh.model.interactor

import io.reactivex.Observable
import ru.jufy.myposh.entity.Artist
import ru.jufy.myposh.entity.Category
import ru.jufy.myposh.model.repository.ArtistRepository
import ru.jufy.myposh.model.repository.ArtworkRepository


class StoreInteractor(val artworkRepository: ArtworkRepository, val artistRepository: ArtistRepository)
    : BaseInteractor(artworkRepository) {
    fun getArtWorks(category:Category?=null, tag:String?=null, artist:Artist? = null): Observable<MutableList<Any>>
            = artworkRepository
            .getArtworks(category?.id, tag, artist?.id)
            .toObservable()
            .flatMapIterable { x->x }
            .map { it as Any }
            .toList()
            .toObservable()
            // add header to list of items depend on source
            .map {
                if (category != null) {
                    it.add(0, category.name)
                }
                if (tag != null) {
                    it.add(0, tag)
                    if (artist != null) {
                        it.add(0, artist)
                    }
                }
                if (artist != null){
                    it.add(0, artist.name)

                }

                return@map it
            }

    fun getArtists() = artistRepository.getArtists()

    fun getTags() = artworkRepository.getTags()
            .toObservable()
            .flatMapIterable { x->x }
            .map { it.name }
            .toList()
            .toObservable()


    fun getCategories() = artworkRepository.getCategories()
}