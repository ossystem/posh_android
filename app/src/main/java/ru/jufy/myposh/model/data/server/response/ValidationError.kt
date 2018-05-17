package com.jufy.mgtshr.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import ru.jufy.myposh.model.data.server.response.ApiError
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import ru.jufy.myposh.model.data.server.response.BaseResponse


/**
 * Created by rolea on 12.04.2018.
 */
class ValidationError {
    var message: String = ""
}

class ValidationListError:BaseResponse() {
    var errors: HashMap<String, String> = hashMapOf()
}

public fun ResponseBody.getErrorMessage(): String {
    return try {
        var errorMessage = ""
        val gson = Gson()
        val responseString = this.string()
        val jsonObj = gson.fromJson(responseString, JsonElement::class.java).asJsonObject
        val error = jsonObj.get("error")
        if (error != null){
            val validationErrorType = object : TypeToken<ApiError<ValidationError>>() {}.type
            val errorApiError = Gson().fromJson<ApiError<ValidationError>>(responseString, validationErrorType)
            errorMessage = errorApiError.error.message
        } else {
            val errorApiError = Gson().fromJson<ValidationListError>(responseString, ValidationListError::class.java)
            for (message in errorApiError.errors.values) errorMessage+= "$message + \n"
        }

        return errorMessage

    } catch (e: Exception) {
        e.message ?: "Unknown error in ResponseBody.getErrorMessage"
    }

}