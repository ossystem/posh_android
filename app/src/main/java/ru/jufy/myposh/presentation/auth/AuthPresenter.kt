package ru.jufy.myposh.presentation.auth

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.MyPoshApplication
import ru.jufy.myposh.Screens
import ru.jufy.myposh.model.interactor.AuthInteractor
import ru.jufy.myposh.presentation.global.BasePresenter
import ru.jufy.myposh.presentation.global.ErrorHandler
import javax.inject.Inject

class AuthPresenter<V : AuthMvpView> @Inject constructor(val interactor: AuthInteractor,
                                                         val errorHandler: ErrorHandler) : BasePresenter<V>() {
    private lateinit var phone: String

    private var isPhoneView: Boolean = true

    fun sendNumberClicked(phone: String) {
        interactor.authenticate(phone)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe({ d ->
                    disposable.add(d)
                    getMvpView()?.togglePhoneProgressVisibility(true)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            this@AuthPresenter.phone = phone
                            isPhoneView = false

                            getMvpView()?.togglePhoneProgressVisibility(false)
                            getMvpView()?.toggleCodeView(true)
                        }, {
                    errorHandler.proceed(it, {
                        getMvpView()?.togglePhoneProgressVisibility(false)
                        getMvpView()?.onError(it)
                    })
                }
                )
    }

    fun sendCodeClicked(code: String) {
        interactor.login(phone, code)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe({ d ->
                    disposable.add(d)
                    getMvpView()?.toggleCodeProgressVisibility(true)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate { getMvpView()?.toggleCodeProgressVisibility(false) }
                .subscribe(
                        {
                            //TODO:Remove when finish to refactor legacy code
                            MyPoshApplication.onNewTokenObtained(it)
                            getMvpView()?.navigateTo(Screens.MAIN_ACTIVITY_SCREEN)
                        },
                        { errorHandler.proceed(it, { getMvpView()?.onError(it) }) }
                )
    }

    fun onBackPressed() {
        if (isPhoneView) {
            //   router.exit()
        } else {
            isPhoneView = true
            getMvpView()?.toggleCodeView(false)
            getMvpView()?.clearCode()
        }
    }
}