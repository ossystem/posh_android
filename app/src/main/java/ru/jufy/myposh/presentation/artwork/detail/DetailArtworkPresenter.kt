package ru.jufy.myposh.presentation.artwork.detail

import com.jufy.mgtshr.ui.base.ChildFragmentPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.R
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.interactor.DetailArtworkInteractor
import ru.jufy.myposh.model.interactor.LikeArtworkInteractor
import ru.jufy.myposh.model.system.ResourceManager
import ru.jufy.myposh.presentation.global.ErrorHandler
import javax.inject.Inject

class DetailArtworkPresenter<V : DetailArtworkMvpView> @Inject constructor(val interactor: DetailArtworkInteractor,
                                                                           val likeArtworkInteractor: LikeArtworkInteractor,
                                                                           val resourceManager: ResourceManager,
                                                                           val errorHandler: ErrorHandler)
    : ChildFragmentPresenter<V>() {

    fun init(artwork: MarketImage) {
        interactor.artwork = artwork
        likeArtworkInteractor.artwork = artwork
        interactor.loadArtwork()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable.add(it) }
                .subscribe({
                    getMvpView()?.setupLikeState(artwork.isFavorite)
                    getMvpView()?.setupPurchaseState(artwork.isPurchased)
                }, {})
    }

    fun buyDownloadClicked() {
        if (interactor.artwork.canDownload()) {
            // since we need download posh to storage and kulon
            // we should check permission on storage and coarse location
            getMvpView()?.checkPermissions()
        } else {
            buy()
        }
    }

    fun downloadArtwork() {
        // download image to storage
        interactor.download()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable.add(it) }
                .subscribe({
                    // then download image to kulon
                    getMvpView()?.showMessage(resourceManager.getString(R.string.image_downloaded))
                    getMvpView()?.installImage()
                }, {
                    errorHandler.proceed(it, { getMvpView()?.onError(it) })
                })

    }

    private fun buy() {
        interactor.buy()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    disposable.add(it)
                    getMvpView()?.updatePurchaseState(true)
                    //getMvpView().showProgress()
                }
                .subscribe({
                    getMvpView()?.showMessage(it.message!!)
                }, {
                    errorHandler.proceed(it, {
                        getMvpView()?.updatePurchaseState(false)
                        getMvpView()?.showMessage(it)
                    })
                })
    }


    fun toggleLike() {
        likeArtworkInteractor
                .toggleLike()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable.add(it) }
                .subscribe({
                    getMvpView()?.showMessage(it.message!!)
                }, { errorHandler.proceed(it, { getMvpView()?.showMessage(it) }) })

    }
}