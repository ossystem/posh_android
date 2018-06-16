package ru.jufy.myposh.entity

class Artist(id:String, name:String, avatar:File?):BaseEntity(id, name)

class ArtistWrapper(val artists:MutableList<Artist>)