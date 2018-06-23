package ru.jufy.myposh.model.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.data.server.response.BaseResponse
import ru.jufy.myposh.model.storage.UserPreferences

/**
 * Created by rolea on 01.03.2018.
 */

open class BaseRepository(val apiService: ApiService, val preferences: UserPreferences) {

    fun isLoggedIn(): Boolean {
        return preferences.isLoggedIn
    }


    fun getBalance(): Single<Long> {
        return apiService.getBalance()
                .subscribeOn(Schedulers.io())
                .map{it.data.total}
    }

    fun clearAll() {
        preferences.singOut()
    }


    fun logout() {
        clearAll()
    }

    fun getReferallLink(): Single<String> {
        return apiService
                .getReferralCode()
                .subscribeOn(Schedulers.io())
                .map{it.data.referral_code}
    }

    fun getReferralCode(): String{
       return preferences.referralCode;
    }

    fun setRefferalCode(referralCode: String) {
        preferences.referralCode = referralCode
    }

    fun removeReferralCode(){
        preferences.referralCode = ""
    }

    fun performReferral(referralCode:String):Observable<BaseResponse>{
        return apiService.performReferral(referralCode)
                .subscribeOn(Schedulers.io())
                .map { it.data }
                .toObservable()
    }

}