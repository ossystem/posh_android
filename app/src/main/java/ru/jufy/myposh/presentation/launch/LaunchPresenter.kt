package ru.jufy.myposh.presentation.launch

import ru.jufy.myposh.Screens
import ru.jufy.myposh.model.interactor.BaseInteractor
import ru.jufy.myposh.presentation.global.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class LaunchPresenter<V:LaunchMvpView> @Inject constructor(val interactor: BaseInteractor, val router: Router):
        BasePresenter<V>(){

    override fun onAttach(mvpView: V) {
        super.onAttach(mvpView)
        if (isViewAttached()) {
            if (interactor.isLoggedIn) {
                router.replaceScreen(Screens.MAIN_ACTIVITY_SCREEN)
            } else {
                router.replaceScreen(Screens.LOGIN_ACTIVITY_SCREEN)
            }
        }
    }
}