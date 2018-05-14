package ru.jufy.myposh.entity

/**
 * Created by BorisDev on 31.07.2017.
 */

class KulonToken(val token:String, val user_id:String?="") {
    constructor(token: String):this(token, "")
}
