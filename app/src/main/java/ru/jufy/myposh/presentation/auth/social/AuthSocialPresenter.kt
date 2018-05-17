package ru.jufy.myposh.presentation.auth.social

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.jufy.myposh.MyPoshApplication
import ru.jufy.myposh.Screens
import ru.jufy.myposh.entity.SocialTypes
import ru.jufy.myposh.model.interactor.AuthInteractor
import ru.jufy.myposh.presentation.global.BasePresenter
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AuthSocialPresenter<V:AuthSocialMvpView> @Inject constructor(val interactor:AuthInteractor,
                                                                   val errorHandler:ErrorHandler,
                                                                   val router: Router): BasePresenter<V>() {
    fun loginSocial(url:String){
        interactor.loginSocial(url)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable.add(it)
                    getMvpView()?.showProgress()
                }
                .doAfterTerminate { getMvpView()?.hideProgress() }
                .subscribe(
                        {
                            //TODO:Remove when finish to refactor legacy code
                            MyPoshApplication.onNewTokenObtained(it)
                            router.newRootScreen(Screens.MAIN_ACTIVITY_SCREEN)
                        },
                        { errorHandler.proceed(it, { getMvpView()?.onError(it) }) })
    }

    fun authSocial(socialType: String){
        interactor.getAuthSocialLink(SocialTypes.fromValue(socialType))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    disposable.add(it)
                    getMvpView()?.showProgress()
                }
                .doAfterTerminate { getMvpView()?.hideProgress() }
                .subscribe({ getMvpView()?.loadUrl(it) },
                        {errorHandler.proceed(it, {getMvpView()?.onError(it)})})
    }
}