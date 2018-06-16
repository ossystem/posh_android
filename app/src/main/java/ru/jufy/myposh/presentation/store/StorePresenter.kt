package ru.jufy.myposh.presentation.store

import com.jufy.mgtshr.ui.base.ChildFragmentPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.jufy.myposh.Screens
import ru.jufy.myposh.entity.Artist
import ru.jufy.myposh.entity.Category
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.interactor.StoreInteractor
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.ui.fragments.ImageGridFragment
import javax.inject.Inject

class StorePresenter<V : StoreMvpView> @Inject constructor(val interactor: StoreInteractor,
                                                           val errorHandler: ErrorHandler) :
        ChildFragmentPresenter<V>(), ImageGridFragment.ArtworkListener {
    private var currentArtworks: MutableList<Any>? = null
    private var allArtworks: MutableList<Any>? = null
    private var artists: MutableList<Artist>? = null

    fun loadArtworks() {
        if (currentArtworks == null) getArtworks()
        else getMvpView()?.updateItems(currentArtworks!!)
    }

    private fun getArtworks(tag: String? = null, artist: Artist? = null, category: Category? = null) {
        interactor.getArtWorks(category, tag, artist)
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
                        if (tag == null && artist== null && category == null)
                            allArtworks = it
                        this.currentArtworks = it
                        getMvpView()?.updateItems(it)
                    }

                }, { errorHandler.proceed(it, { getMvpView()?.onError(it) }) })
    }


    fun getTags() {
        interactor.getTags()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    disposable.add(it)
                    getMvpView()?.toggleArcLayoutProgressVisibility(true)
                }
                .doAfterTerminate { getMvpView()?.toggleArcLayoutProgressVisibility(false) }
                .subscribe({ if (!it.isEmpty()) getMvpView()?.updateTags(it) },
                        { errorHandler.proceed(it, { getMvpView()?.onError(it) }) })
    }

    fun getCategories() {
        interactor.getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    disposable.add(it)
                    getMvpView()?.toggleArcLayoutProgressVisibility(true)
                }
                .doAfterTerminate { getMvpView()?.toggleArcLayoutProgressVisibility(false) }
                .subscribe({ if (!it.isEmpty()) getMvpView()?.updateCategories(it) },
                        { errorHandler.proceed(it, { getMvpView()?.onError(it) }) })
    }

    fun loadArtWorksByTag(tag: String) {
        getArtworks(tag = tag)
    }

    fun loadArtist(name: String) {
        artists?.let {
            val artist = it.find { name == it.name }
            if (artist != null) {
                getArtworks(artist = artist)
            } else {
                getMvpView()?.changeNotFoundState(true)
                getMvpView()?.changeNotFoundTitle("Нет результатов")
            }
        }
    }

    fun loadCategory(category: Category) {
        getArtworks(category = category)
    }

    fun getArtists() {
        if (artists == null) {
            interactor.getArtists()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        disposable.add(it)
                        getMvpView()?.toggleArcLayoutProgressVisibility(true)
                    }
                    .doAfterTerminate { getMvpView()?.toggleArcLayoutProgressVisibility(false) }
                    .subscribe({
                        this.artists = it
                        if (!it.isEmpty()) getMvpView()?.updateArtists(it.map { it.name }.toMutableList())
                    }, { errorHandler.proceed(it, { getMvpView()?.onError(it) }) })

        } else getMvpView()?.updateArtists(artists!!.map { it.name }.toMutableList())
    }

    override fun artworkClicked(image: MarketImage?) {
        router.navigateTo(Screens.DETAIL_ARTWORK, image)
    }

    fun clearFilterClicked() {
        if (allArtworks!=null) {
            currentArtworks = allArtworks
            getMvpView()?.updateItems(currentArtworks!!)
        } else {
            getArtworks()
        }
    }
}