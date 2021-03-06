package ru.jufy.myposh.presentation.global

import com.jufy.mgtshr.extensions.getErrorMessage
import retrofit2.HttpException
import ru.jufy.myposh.R
import ru.jufy.myposh.Screens
import ru.jufy.myposh.model.interactor.AuthInteractor
import ru.jufy.myposh.model.system.ResourceManager
import ru.terrakok.cicerone.Router
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class ErrorHandler @Inject constructor(
        private val authInteractor: AuthInteractor,
        val resourceManager: ResourceManager,
        val router:Router
) {

    /*private val authErrorRelay = PublishRelay.create<Boolean>()*/

    init {
        subscribeOnAuthErrors()
    }

    fun proceed(error: Throwable, messageListener: (String) -> Unit = {}) {
        when (error) {
            is HttpException -> messageListener(error.response().errorBody()!!.getErrorMessage())
            is SocketTimeoutException -> messageListener(resourceManager.getString(R.string.no_connectivity_error))
            is IOException -> messageListener(resourceManager.getString(R.string.no_connectivity_error))
            else -> messageListener(resourceManager.getString(R.string.unknown_error))
        }
    }

    private fun subscribeOnAuthErrors() {
        /*authErrorRelay
                .throttleFirst(50, TimeUnit.MILLISECONDS)
                .observeOn(schedulers.ui())
                .subscribe { logout() }*/
    }

    public fun logout() {
        authInteractor.logout()
        router.newRootScreen(Screens.LOGIN_ACTIVITY_SCREEN)
    }
}
