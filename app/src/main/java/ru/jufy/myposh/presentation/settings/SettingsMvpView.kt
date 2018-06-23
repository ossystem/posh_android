package ru.jufy.myposh.presentation.settings

import ru.jufy.myposh.ui.global.MvpView

interface SettingsMvpView:MvpView {
    fun shareReferralCode(code:String)
}