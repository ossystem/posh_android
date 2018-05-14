package com.jufy.mgtshr.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import ru.jufy.myposh.models.data.server.response.ApiError

/**
 * Created by rolea on 12.04.2018.
 */
class ValidationError {
    var message: String = ""
}

public fun ResponseBody.getErrorMessage(code: Int): String {
    return try {

        val validationErrorType = object : TypeToken<ApiError<ValidationError>>() {}.type
        val errorApiError = Gson().fromJson<ApiError<ValidationError>>(this.string(), validationErrorType)
        errorApiError.error.message

    } catch (e: Exception) {
        e.message ?: "Unknown error in ResponseBody.getErrorMessage"
    }

}