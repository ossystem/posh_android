package ru.jufy.myposh.model.repository

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.entity.KulonToken
import ru.jufy.myposh.entity.SocialTypes
import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.data.server.response.BaseResponse
import ru.jufy.myposh.model.storage.UserPreferences
import javax.inject.Inject

class AuthRepository @Inject constructor(apiService: ApiService, preferences: UserPreferences) :
        BaseRepository(apiService, preferences) {

    fun authenticate(phone: String): Single<BaseResponse> {
        return apiService.authenticate(phone).map { it.data }

    }

    fun saveToken(token: String) {
        preferences.token = token
    }

    fun login(phone: String, code: String): Single<KulonToken> {
        return apiService.login(phone, code).map{ it.data }
    }

    fun getAuthSocialLink(socialType:SocialTypes):Single<String>{
        return apiService.authSocial(socialType)
                .subscribeOn(Schedulers.io())
                .map { it.data.link }
    }

    fun loginSocial(url: String): Single<KulonToken> {
        return apiService.loginSocial(url)
                .subscribeOn(Schedulers.io())
                .map { it.data }
    }
}