package com.jufy.mgtshr.ui.base


import ru.jufy.myposh.Screens
import ru.jufy.myposh.presentation.global.BasePresenter
import ru.jufy.myposh.ui.global.ChildFragmentMvpPresenter
import ru.jufy.myposh.ui.global.MvpView
import ru.terrakok.cicerone.Router

/**
 * Created by rolea on 06.10.2017.
 */

open class ChildFragmentPresenter<V : MvpView> : BasePresenter<V>(), ChildFragmentMvpPresenter<V> {

    lateinit var router: Router


    override fun onBackPressed() {
        router.exit()
    }

    fun onAuthError() {
        router.newRootScreen(Screens.LOGIN_ACTIVITY_SCREEN)
    }
}
