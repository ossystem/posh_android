package ru.jufy.myposh.model.interactor

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.jufy.myposh.entity.KulonToken
import ru.jufy.myposh.entity.SocialTypes
import ru.jufy.myposh.model.repository.AuthRepository

class AuthInteractor(val authRepository: AuthRepository) : BaseInteractor(authRepository) {
    fun authenticate(phone: String) = authRepository.authenticate(phone)

    fun login(phone: String, code: String) = authRepository.login(phone, code)
            .map(mapToTokenAndSave())

    fun getAuthSocialLink(socialTypes: SocialTypes) = authRepository.getAuthSocialLink(socialTypes)

    fun loginSocial(url: String) = authRepository.loginSocial(url)
            .map(mapToTokenAndSave())

    private fun mapToTokenAndSave(): (KulonToken) -> KulonToken {
        return { t ->
            Observable.create<Any> { subscriber ->
                authRepository.saveToken(t.token)
                subscriber.onComplete()
            }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            t
        }
    }
}