package ru.jufy.myposh.model.data.server

import io.reactivex.Single
import retrofit2.http.*
import ru.jufy.myposh.entity.KulonToken
import ru.jufy.myposh.entity.SocialTypes
import ru.jufy.myposh.model.data.server.response.BaseResponse
import ru.jufy.myposh.model.data.server.response.DataWrapper
import ru.jufy.myposh.model.data.server.response.LinkResponse

interface ApiService {
    @FormUrlEncoded
    @POST("api/v1/auth")
    fun authenticate(@Field("phone") phone:String):Single<DataWrapper<BaseResponse>>

    @FormUrlEncoded
    @POST("api/v1/login")
    fun login(@Field("phone") phone:String, @Field("code") code:String):Single<DataWrapper<KulonToken>>

    @GET("api/v1/social/{socialType}")
    fun authSocial(@Path("socialType")socialType:SocialTypes):Single<DataWrapper<LinkResponse>>

    @GET
    fun loginSocial(@Url url:String):Single<DataWrapper<KulonToken>>
}