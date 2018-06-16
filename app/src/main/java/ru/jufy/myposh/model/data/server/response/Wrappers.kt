package ru.jufy.myposh.model.data.server.response

import ru.jufy.myposh.entity.BaseEntity
import ru.jufy.myposh.entity.Category
import ru.jufy.myposh.entity.MarketImage

class DataWrapper<T>(val data:T)

class ArtworkWrapper(val artworks:MutableList<MarketImage>)

class PurchasesWrapper(val purchases:MutableList<Purchase>)

class Purchase(val id:String, val artwork:MarketImage)

class SingleArtworkWrapper(val artwork:MarketImage)

class TagsWrapper(val tags:MutableList<BaseEntity>)

class CategoryWrapper(val categories:MutableList<Category>)

class BalanceWrapper(val total:Long)


