package ru.jufy.myposh.ui.global

import android.support.annotation.StringRes

interface MvpView {
    fun onAuthError()
    fun showProgress()
    fun hideProgress()
    fun showMessage(message: String)
    fun showMessage(@StringRes message:Int)
    fun onError(@StringRes resId: Int)
    fun onError(message: String)
    fun hideKeyboard()
    fun onLoadingSuccess()
    fun onLoadingError()
    fun showMessage(title: String, message: String)
}