package ru.jufy.myposh.model.interactor

import io.branch.referral.Branch
import ru.jufy.myposh.model.repository.BaseRepository
import javax.inject.Inject


/**
 * Created by rolea on 17.11.2017.
 */

open class BaseInteractor @Inject constructor(internal var repository: BaseRepository) {

    val isLoggedIn: Boolean
        get() = repository.isLoggedIn()

    fun logout() {
        Branch.getInstance().logout()
        repository.logout()
    }

    fun saveReferral(referralCode: String) {
        repository.setRefferalCode(referralCode)
    }
}
