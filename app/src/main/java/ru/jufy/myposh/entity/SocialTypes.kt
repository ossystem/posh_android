package ru.jufy.myposh.entity

enum class SocialTypes(val value:String) {
    FB("facebook"), VK("vk"), INSTAGRAM("instagram");

    companion object {
        fun fromValue(value: String):SocialTypes{
            return when (value) {
                FB.toString() -> FB
                VK.toString() -> VK
                INSTAGRAM.toString() -> INSTAGRAM
                else -> {
                    FB
                }
            }
        }
    }

    override fun toString() = value
}