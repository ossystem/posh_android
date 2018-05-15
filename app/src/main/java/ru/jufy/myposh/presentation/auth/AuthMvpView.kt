package ru.jufy.myposh.presentation.auth

import ru.jufy.myposh.ui.global.MvpView

interface AuthMvpView:MvpView {
    fun toggleCodeView(isCodeView:Boolean)
    fun togglePhoneProgressVisibility(isLoading:Boolean)
    fun toggleCodeProgressVisibility(isLoading: Boolean)
    fun navigateTo(screenKey: String, data:Any? = null)
    fun clearCode()
}