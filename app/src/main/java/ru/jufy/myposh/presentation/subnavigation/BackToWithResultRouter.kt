package com.jufy.mgtshr.ui.subnavigation

import ru.terrakok.cicerone.Router

/**
 * Created by rolea on 10.10.2017.
 */

class BackToWithResultRouter : Router() {
    fun backToWithResult(screenKey: String, resultCode: Int, result: Any) {
        backTo(screenKey)
        sendResult(resultCode, result)
    }
}
