package ru.jufy.myposh.model.data.server

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.jufy.myposh.entity.KulonToken
import ru.jufy.myposh.model.data.server.response.BaseResponse
import ru.jufy.myposh.model.data.server.response.DataWrapper

interface ApiService {
    @FormUrlEncoded
    @POST("api/v1/auth")
    fun authenticate(@Field("phone") phone:String):Single<DataWrapper<BaseResponse>>

    @FormUrlEncoded
    @POST("api/v1/login")
    fun login(@Field("phone") phone:String, @Field("code") code:String):Single<DataWrapper<KulonToken>>
}