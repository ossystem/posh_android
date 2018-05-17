package ru.jufy.myposh.presentation.auth.social

import ru.jufy.myposh.ui.global.MvpView

interface AuthSocialMvpView:MvpView {
    fun loadUrl(it: String)
}