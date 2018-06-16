package ru.jufy.myposh.entity

class File(val link: String, val height:Int, val width:Int, val mime:String)

class DeviceInfo(val id:String, val name:String, val code:String, val icon:File)


