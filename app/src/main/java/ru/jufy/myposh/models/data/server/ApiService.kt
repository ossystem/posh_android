package ru.jufy.myposh.models.data.server

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.POST
import ru.jufy.myposh.entity.KulonToken
import ru.jufy.myposh.models.data.server.response.BaseResponse
import ru.jufy.myposh.models.data.server.response.DataWrapper

interface ApiService {
    @POST("api/v1/auth")
    fun authenticate(@Field("phone") phone:String):Single<DataWrapper<BaseResponse>>

    @POST("api/v1/login")
    fun authenticate(@Field("phone") phone:String, @Field("code") code:String):Single<DataWrapper<KulonToken>>
}