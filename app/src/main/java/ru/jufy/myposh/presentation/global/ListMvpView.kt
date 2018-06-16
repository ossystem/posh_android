package ru.jufy.myposh.presentation.global

import ru.jufy.myposh.ui.global.EmptyView
import ru.jufy.myposh.ui.global.MvpView

/**
 * Created by rolea on 7/21/2017.
 */

interface ListMvpView<T> : MvpView, EmptyView {
    fun updateItems(items: MutableList<T>)
    fun setRefreshingState(refreshingState: Boolean)
    fun setupList()
}
