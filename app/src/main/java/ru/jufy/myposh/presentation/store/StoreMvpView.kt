package ru.jufy.myposh.presentation.store

import ru.jufy.myposh.entity.Category
import ru.jufy.myposh.presentation.global.ListMvpView

interface StoreMvpView:ListMvpView<Any> {
    fun updateTags(mutableList: MutableList<String>)
    fun updateCategories(mutableList: MutableList<Category>)
    fun toggleArcLayoutProgressVisibility(isVisible:Boolean)
    fun updateArtists(mutablelist: MutableList<String>)
}