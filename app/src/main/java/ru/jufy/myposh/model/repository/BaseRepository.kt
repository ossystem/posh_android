package ru.jufy.myposh.model.repository

import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.storage.UserPreferences

/**
 * Created by rolea on 01.03.2018.
 */

open class BaseRepository(val apiService: ApiService, val preferences: UserPreferences) {

    fun isLoggedIn(): Boolean {
        return preferences.isLoggedIn
    }


    fun clearAll() {
        preferences.singOut()
    }


    fun logout() {
        clearAll()
    }

}