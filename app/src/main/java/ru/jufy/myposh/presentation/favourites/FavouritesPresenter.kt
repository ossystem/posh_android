package ru.jufy.myposh.presentation.favourites

import com.jufy.mgtshr.ui.base.ChildFragmentPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.jufy.myposh.R
import ru.jufy.myposh.Screens
import ru.jufy.myposh.entity.Image
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.interactor.FavouritesInteractor
import ru.jufy.myposh.model.system.ResourceManager
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.ui.fragments.ImageGridFragment
import java.util.*
import javax.inject.Inject

class FavouritesPresenter<V:FavouritesMvpView> @Inject constructor(val interactor: FavouritesInteractor,
                                                                   val resourceManager: ResourceManager,
                                                                   val errorHandler: ErrorHandler):ChildFragmentPresenter<V>(), ImageGridFragment.ArtworkListener {
    override fun artworkClicked(image: MarketImage?) {
        router.navigateTo(Screens.DETAIL_ARTWORK, image)
    }

    private var currentArtworks: MutableList<Any>? = null

    fun loadArtworks() {
        /*if (currentArtworks == null) getArtworks()
        else getMvpView()?.updateItems(currentArtworks!!)*/
        getArtworks()
    }

    private fun getArtworks() {
        interactor.getFavourites()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    disposable.add(it)
                    getMvpView()?.showProgress()
                }
                .doAfterTerminate { getMvpView()?.hideProgress() }
                .subscribe({
                    if (it.isEmpty()) {
                        getMvpView()?.changeNotFoundState(true)
                        getMvpView()?.changeNotFoundTitle("Нет результатов")
                    } else {
                        this.currentArtworks = it
                        getMvpView()?.updateItems(it)
                    }

                }, { errorHandler.proceed(it, { getMvpView()?.onError(it) }) })
    }

    fun deleteItems(images:List<MarketImage>){
        interactor.deleteFavourites(images)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    disposable.add(it)
                    getMvpView()?.showProgress()
                }
                .subscribe({
                    val successDeleted = mutableListOf<FavouritesInteractor.ImageDeletedResponse>()
                    var countNotDeketed: Int = 0
                    for (response in it){
                        if (response.isDeleted) successDeleted.add(response)
                        else countNotDeketed++
                    }
                    if (countNotDeketed>0)
                        getMvpView()?.showMessage(resourceManager.getString(R.string.image_deletion_failed)+countNotDeketed)


                    val newData = ArrayList<Any>()
                    for (dataItem in currentArtworks!!) {
                        var found = false
                        for (unlikedImage in successDeleted) {
                            if (dataItem is Image) {
                                if (dataItem.isMe(unlikedImage.id)) {
                                    found = true
                                    break
                                }
                            }
                        }
                        if (!found) {
                            newData.add(dataItem)
                        }
                    }
                    currentArtworks = newData
                    getMvpView()?.updateItems(currentArtworks!!)

                }) { errorHandler.proceed(it, { getMvpView()?.onError(it) }) }
    }
}