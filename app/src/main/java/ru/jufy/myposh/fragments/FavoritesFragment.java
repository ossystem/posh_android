package ru.jufy.myposh.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import ru.jufy.myposh.adapters.ImageAdapter;
import ru.jufy.myposh.adapters.ImageGridDecoration;
import ru.jufy.myposh.R;
import ru.jufy.myposh.data.ImageRepository;
import ru.jufy.myposh.views.ArcLayout;


public class FavoritesFragment extends ImageGridFragment {


    public FavoritesFragment() {
        // Required empty public constructor
    }


    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        setupGrid(null);
        return rootView;
    }



}
