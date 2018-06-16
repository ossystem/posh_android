package ru.jufy.myposh.model.interactor

import io.reactivex.Single
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.data.server.response.BaseResponse
import ru.jufy.myposh.model.repository.ArtworkRepository

class LikeArtworkInteractor(val artworkRepository: ArtworkRepository) {
    lateinit var artwork: MarketImage

    fun toggleLike(): Single<BaseResponse> {
        artwork.isFavorite = !artwork.isFavorite
        return artworkRepository.toggleLike(artwork.id, artwork.isFavorite)
    }
}