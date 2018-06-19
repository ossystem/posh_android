package ru.jufy.myposh.model.interactor

import io.reactivex.Observable
import io.reactivex.Single
import okio.Okio
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.repository.ArtworkRepository
import java.io.File


class DetailArtworkInteractor(val artworkRepository: ArtworkRepository) {
    lateinit var artwork: MarketImage

    fun buy() = artworkRepository
            .getAcquisition(artwork.id, "artwork")
            .flatMap {
                artworkRepository.buy(it.purchaseParametrs)
            }.map {
                artwork.isPurchased = true
                return@map it
            }



    fun download(): Observable<File> {
        return artworkRepository.downloadArtwork(artwork.id, artwork.devices.get(0).id)
                .flatMap {

                    val file = artwork.createTempFile()

                    val sink = Okio.buffer(Okio.sink(file))
                    sink.write(it.body()!!.bytes())
                    sink.close()

                    artwork.tempFile = file
                    return@flatMap Observable.just(file)

                }
    }

    fun loadArtwork():Single<MarketImage> {
        return artworkRepository.loadArtwork(artwork.id)
                .map {
                    artwork.acqusitionParam = it.acqusitionParam
                    return@map it
                }
    }
}