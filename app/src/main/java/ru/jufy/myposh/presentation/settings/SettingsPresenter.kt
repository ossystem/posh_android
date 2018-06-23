package ru.jufy.myposh.presentation.settings

import com.jufy.mgtshr.ui.base.ChildFragmentPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.jufy.myposh.model.interactor.SettingsInteractor
import ru.jufy.myposh.model.system.ResourceManager
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.ui.settings.SettingsListener
import javax.inject.Inject

class SettingsPresenter<V : SettingsMvpView> @Inject constructor(val interactor: SettingsInteractor,
                                                                 val resourceManager: ResourceManager,
                                                                 val errorHandler: ErrorHandler) : ChildFragmentPresenter<V>(),
        SettingsListener {
    override fun logoutClicked() {
        errorHandler.logout()
    }

    private var referralCode: String? = null

    override fun shareClicked() {
        if (referralCode == null)
            interactor.getReferralLink()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { disposable.add(it) }
                    .subscribe({
                        referralCode = it
                        getMvpView()?.shareReferralCode(it)
                    }, { errorHandler.proceed(it, {}) })

        else getMvpView()?.shareReferralCode(referralCode!!)
    }

}