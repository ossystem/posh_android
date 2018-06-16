package ru.jufy.myposh.model.repository

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.entity.Artist
import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.storage.UserPreferences
import javax.inject.Inject

class ArtistRepository@Inject constructor(apiService: ApiService, preferences: UserPreferences) :
        BaseRepository(apiService, preferences) {

    fun getArtists(): Single<MutableList<Artist>> {
        return apiService.getArtists()
                .subscribeOn(Schedulers.io())
                .map { it.data.artists }
    }
}