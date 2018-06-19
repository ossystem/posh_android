package ru.jufy.myposh.model.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import ru.jufy.myposh.entity.BaseEntity
import ru.jufy.myposh.entity.Category
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.data.server.response.Acquisition
import ru.jufy.myposh.model.data.server.response.BaseResponse
import ru.jufy.myposh.model.data.server.response.PurchaseData
import ru.jufy.myposh.model.storage.UserPreferences
import javax.inject.Inject

class ArtworkRepository @Inject constructor(apiService: ApiService, preferences: UserPreferences) :
        BaseRepository(apiService, preferences) {

    fun getArtworks(categoryId: String? = null, tag: String? = null, artistId: String? = null)
            : Single<MutableList<MarketImage>> {
        return apiService.getArtworks(categoryId, tag, artistId)
                .subscribeOn(Schedulers.io())
                .map { it.data.artworks }
    }

    fun getTags():Single<MutableList<BaseEntity>>{
        return apiService.getTags()
                .subscribeOn(Schedulers.io())
                .map { it.data.tags }
    }

    fun getCategories():Single<MutableList<Category>>{
        return apiService.getCategories()
                .subscribeOn(Schedulers.io())
                .map { it.data.categories }
    }

    fun getAcquisition( id:String, type:String):Single<Acquisition>{
        return apiService.getAcquisition(id, type)
                .subscribeOn(Schedulers.io())
                .map { it.data.acquisition }
    }

    fun buy(purchaseData:PurchaseData):Single<BaseResponse>{
        return apiService.purchase(purchaseData)
                .subscribeOn(Schedulers.io())
                .map { it.data }
    }

    fun getOwnedArtworks():Single<MutableList<MarketImage>>{
        return apiService.getOwnedArtworks()
                .subscribeOn(Schedulers.io())
                .map { it.data.purchases }
                .toObservable()
                .flatMapIterable { x->x }
                .map {
                    val result = it.artwork
                    result.isPurchased = true
                    return@map result
                }
                .toList()
    }

    fun toggleLike(id:String, isLiked:Boolean):Single<BaseResponse>{
        return if (isLiked){
            apiService.likeArtwork(id)
                    .subscribeOn(Schedulers.io())
                    .map { it.data }
        } else
            apiService.dislikeArtwork(id)
                    .subscribeOn(Schedulers.io())
                    .map { it.data }
    }

    fun downloadArtwork(artistId: String, deviceId:String):Observable<Response<ResponseBody>>{
        return apiService.downloadArtwork(artistId, deviceId)
    }

    fun loadArtwork(id: String):Single<MarketImage> {
        return apiService.getArtwork(id).map { it.data.artwork }
                .subscribeOn(Schedulers.io())
    }
}