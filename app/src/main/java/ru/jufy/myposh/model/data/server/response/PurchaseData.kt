package ru.jufy.myposh.model.data.server.response

import com.google.gson.annotations.SerializedName
import ru.jufy.myposh.entity.File

class PurchaseData (val id:String, val type:String, val seller_id:String, val price:Int)

class Seller(val id: String, val name:String, val avatar:File?)

class PurchasableData(val id: String, val image:File)

class PurchaseParametrs(val id:String, val type:String, val seller_id: String, val price: Int)

class Acquisition(val price: Int, @SerializedName("purchase_params")val purchaseParametrs: PurchaseData,
                  @SerializedName("purchasable")val purchasableData: PurchasableData,
                  val seller:Seller)

class AcqusitionParam(val id: String, val type: String)

class AcquisitionWrapper(val acquisition: Acquisition)