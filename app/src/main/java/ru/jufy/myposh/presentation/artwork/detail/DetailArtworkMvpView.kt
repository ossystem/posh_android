package ru.jufy.myposh.presentation.artwork.detail

import ru.jufy.myposh.ui.global.BackButtonListener
import ru.jufy.myposh.ui.global.MvpView

interface DetailArtworkMvpView:MvpView, BackButtonListener {
    fun updateLikeState(isLiked:Boolean)
    fun updatePurchaseState(isPurchased:Boolean)
    fun installImage()
    fun checkPermissions()
    fun setupLikeState(favorite: Boolean)
    fun setupPurchaseState(purchased: Boolean)
}