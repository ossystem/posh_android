package ru.jufy.myposh.presentation.library

import ru.jufy.myposh.presentation.global.ListMvpView

interface LibraryMvpView: ListMvpView<Any> {
    fun updateBalance(balance:Long)
}