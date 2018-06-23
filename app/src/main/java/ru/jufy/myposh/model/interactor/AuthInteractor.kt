package ru.jufy.myposh.model.interactor

import io.branch.referral.Branch
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.MyPoshApplication
import ru.jufy.myposh.entity.KulonToken
import ru.jufy.myposh.entity.SocialTypes
import ru.jufy.myposh.model.repository.AuthRepository

class AuthInteractor(val authRepository: AuthRepository) : BaseInteractor(authRepository) {
    fun authenticate(phone: String) = authRepository.authenticate(phone)

    fun login(phone: String, code: String) = authRepository.login(phone, code)
            .map(saveToken())
            .toObservable()
            .flatMap {
                return@flatMap handleReferral(it)
            }

    fun getAuthSocialLink(socialTypes: SocialTypes) = authRepository.getAuthSocialLink(socialTypes)

    fun loginSocial(url: String) = authRepository.loginSocial(url)
            .map(saveToken())
            .toObservable()
            .flatMap {
                return@flatMap handleReferral(it)
            }

    private fun handleReferral(it: KulonToken): Observable<Boolean>? {
        if (!it.user_id.isNullOrEmpty()) {
            Branch.getInstance().setIdentity(it.user_id!!)
            if (authRepository.isFromReferral()) {
                return authRepository
                        .performReferral(authRepository.getReferralCode())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap {
                            authRepository.removeReferralCode()
                            Observable.just(true)
                        }
                        .onErrorReturn { false }
                        .observeOn(Schedulers.io())
            } else return Observable.just(true)
        } else return Observable.just(true)
    }

    private fun saveToken(): (KulonToken) -> KulonToken {
        return { t ->
            Observable.create<Any> { subscriber ->
                //TODO:Remove when finish to refactor legacy code
                MyPoshApplication.onNewTokenObtained(t)

                authRepository.saveToken(t.token)
                subscriber.onComplete()
            }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            t
        }
    }
}