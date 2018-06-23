package ru.jufy.myposh.model.data.server

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import ru.jufy.myposh.entity.ArtistWrapper
import ru.jufy.myposh.entity.KulonToken
import ru.jufy.myposh.entity.SocialTypes
import ru.jufy.myposh.model.data.server.response.*

interface ApiService {
    @FormUrlEncoded
    @POST("api/v1/auth")
    fun authenticate(@Field("phone") phone: String): Single<DataWrapper<BaseResponse>>

    @FormUrlEncoded
    @POST("api/v1/login")
    fun login(@Field("phone") phone: String, @Field("code") code: String): Single<DataWrapper<KulonToken>>

    @GET("api/v1/social/{socialType}")
    fun authSocial(@Path("socialType") socialType: SocialTypes): Single<DataWrapper<LinkResponse>>

    @GET
    fun loginSocial(@Url url: String): Single<DataWrapper<KulonToken>>

    @GET("api/v1/artworks")
    fun getArtworks(@Query("category") category: String? = null,
                    @Query("search") search: String? = null,
                    @Query("artist") artist: String? = null): Single<DataWrapper<ArtworkWrapper>>

    @GET("api/v1/tags")
    fun getTags(): Single<DataWrapper<TagsWrapper>>

    @GET("api/v1/categories")
    fun getCategories(): Single<DataWrapper<CategoryWrapper>>

    @GET("api/v1/artists")
    fun getArtists(): Single<DataWrapper<ArtistWrapper>>

    @GET("api/v1/acquisition")
    fun getAcquisition(@Query("id") id:String, @Query("type") type:String):Single<DataWrapper<AcquisitionWrapper>>

    @POST("api/v1/purchase")
    fun purchase(@Body purchaseData: PurchaseData):Single<DataWrapper<BaseResponse>>

    @POST("api/v1/artworks/favorites/{id}")
    fun likeArtwork(@Path("id") artworkId:String, @Body emptyString: String = ""):Single<DataWrapper<BaseResponse>>

    @DELETE("api/v1/artworks/favorites/{id}")
    fun dislikeArtwork(@Path("id") artworkId:String):Single<DataWrapper<BaseResponse>>

    @GET("api/v1/artworks/owned/")
    fun getOwnedArtworks():Single<DataWrapper<PurchasesWrapper>>

    @GET("api/v1/artworks/favorites/")
    fun getFavourites():Single<DataWrapper<ArtworkWrapper>>

    @GET("api/v1/artworks/owned/{artworkId}/download-stream")
    @Streaming
    fun downloadArtwork(@Path("artworkId")artworkId:String, @Query("device_id")deviceId:String ):Observable<Response<ResponseBody>>

    @GET("api/v1/artworks/{id}")
    fun getArtwork(@Path("id") id:String):Single<DataWrapper<SingleArtworkWrapper>>

    @GET("/api/v1/balance")
    fun getBalance(): Single<DataWrapper<BalanceWrapper>>

    @GET("/api/v1/referral/code")
    fun getReferralCode():Single<DataWrapper<Referral>>

    @FormUrlEncoded
    @POST("/api/v1/referral/perform")
    fun performReferral(@Field("refferal_code") refferalCode:String):Single<DataWrapper<BaseResponse>>
}