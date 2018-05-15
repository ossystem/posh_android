package ru.jufy.myposh.model.storage

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.telephony.TelephonyManager

import com.securepreferences.SecurePreferences

import java.io.UnsupportedEncodingException
import java.util.UUID

/**
 * Created by rolea on 5/14/2017.
 */

class UserPreferences(context: Context) {
    private val USER_TOKEN_KEY = "USER_TOKEN_KEY"

    private val preferences: SharedPreferences

    var token: String
        get() = preferences.getString(USER_TOKEN_KEY, "")
        set(id) = preferences.edit().putString(USER_TOKEN_KEY, id).apply()

    var userId: Long
        get() = preferences.getLong(USER_ID_KEY, -1)
        set(id) = preferences.edit().putLong(USER_ID_KEY, id).apply()

    var login: String
        get() = preferences.getString(USER_LOGIN_KEY, "")
        set(id) = preferences.edit().putString(USER_LOGIN_KEY, id).apply()

    val uuid: String
        get() {
            val id = preferences.getString(PREFS_DEVICE_ID, null)
            val uuid = UUID.fromString(id)

            return uuid.toString()

        }

    val isLoggedIn: Boolean
        get() = token != ""

    init {
        preferences = SecurePreferences(context)
        //  initUUID(context);
    }

    private fun initUUID(context: Context) {
        val uuid: UUID
        val id = preferences.getString(PREFS_DEVICE_ID, null)
        if (id == null) {
            val androidId = Settings.Secure.getString(
                    context.contentResolver, Settings.Secure.ANDROID_ID)
            // Use the Android ID unless it's broken, in which case
            // fallback on deviceId,
            // unless it's not available, then fallback on a random
            // number which we store to a prefs file
            try {
                if ("9774d56d682e549c" != androidId) {
                    uuid = UUID.nameUUIDFromBytes(androidId
                            .toByteArray(charset("utf8")))
                } else {
                    val deviceId = (context
                            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
                            .deviceId
                    uuid = if (deviceId != null)
                        UUID
                                .nameUUIDFromBytes(deviceId
                                        .toByteArray(charset("utf8")))
                    else
                        UUID
                                .randomUUID()
                }
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }

            // Write the value out to the prefs file
            preferences.edit()
                    .putString(PREFS_DEVICE_ID, uuid.toString())
                    .apply()
        }
    }

    fun singOut() {
        val editor = preferences.edit()
        editor.remove(USER_ID_KEY)
        editor.remove(USER_LOGIN_KEY)
        editor.remove(USER_TOKEN_KEY)
        editor.remove(PREFS_DEVICE_ID)
        editor.apply()
    }

    companion object {
        private val USER_ID_KEY = "USER_ID_KEY"
        private val USER_LOGIN_KEY = "USER_LOGIN_KEY"
        private val PREFS_DEVICE_ID = "PREFS_DEVICE_ID"
    }
}
