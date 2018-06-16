package ru.jufy.myposh.ui.global;


import com.jufy.mgtshr.ui.subnavigation.BackToWithResultRouter;

import ru.jufy.myposh.presentation.global.MvpPresenter;

/**
 * Created by rolea on 06.10.2017.
 */

public interface ChildFragmentMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void onBackPressed();
}
