package ru.jufy.myposh.presentation.library

import com.jufy.mgtshr.ui.base.ChildFragmentPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.Screens
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.interactor.LibraryInteractor
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.jufy.myposh.ui.fragments.ImageGridFragment
import javax.inject.Inject


class LibraryPresenter<V:LibraryMvpView> @Inject constructor(
        val interactor:LibraryInteractor,
        val errorHandler: ErrorHandler):ChildFragmentPresenter<V>(),
         ImageGridFragment.ArtworkListener{

    override fun artworkClicked(image: MarketImage?) {
        router.navigateTo(Screens.DETAIL_ARTWORK, image)
    }

    fun loadData(){
        interactor.loadData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { disposable.add(it) }
                .doAfterTerminate{getMvpView()?.hideProgress()}
                .subscribe({
                    getMvpView()?.updateBalance(it.second)
                    getMvpView()?.updateItems(it.first.map { it as Any }.toMutableList())
                }, {errorHandler.proceed(it, {getMvpView()?.onError(it)})})
    }


}