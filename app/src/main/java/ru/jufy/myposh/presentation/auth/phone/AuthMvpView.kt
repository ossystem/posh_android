package ru.jufy.myposh.presentation.auth.phone

import ru.jufy.myposh.ui.global.MvpView

interface AuthMvpView:MvpView {
    fun toggleCodeView(isCodeView:Boolean)
    fun togglePhoneProgressVisibility(isLoading:Boolean)
    fun toggleCodeProgressVisibility(isLoading: Boolean)
    fun clearCode()
}