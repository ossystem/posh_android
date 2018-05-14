package ru.jufy.myposh.presentation.global

import io.reactivex.disposables.CompositeDisposable
import ru.jufy.myposh.ui.global.MvpView

class BasePresenter<V:MvpView>:MvpPresenter<V> {
    private var mMvpView: V? = null
    protected var disposable: CompositeDisposable = CompositeDisposable()


    override fun onAttach(mvpView: V) {
        mMvpView = mvpView
        disposable = CompositeDisposable()
    }

    override fun onDetach() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        mMvpView = null
    }

    override fun onResume() {

    }


    override fun onPause() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    fun isViewAttached(): Boolean {
        return mMvpView != null
    }

    fun getMvpView(): V? {
        return mMvpView
    }

}