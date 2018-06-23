package ru.jufy.myposh.model.interactor

import ru.jufy.myposh.model.repository.BaseRepository

class SettingsInteractor(val repository: BaseRepository) {
    fun getReferralLink() = repository.getReferallLink()
}