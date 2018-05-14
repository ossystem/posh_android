package ru.jufy.myposh.presentation.global;


import ru.jufy.myposh.ui.global.MvpView;

/**
 * Created by rolea on 4/22/2017.
 */

public interface MvpPresenter<V extends MvpView> {
    void onAttach(V mvpView);
    void onPause();
    void onDetach();

    void onResume();
}
