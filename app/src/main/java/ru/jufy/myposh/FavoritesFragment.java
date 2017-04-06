package ru.jufy.myposh;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;


public class FavoritesFragment extends Fragment {
    View rootView;
    ImageAdapter adapter;
    GridView imgGrid;
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
        setupGrid();
        return rootView;
    }

    private void setupGrid() {
        //calculate dimens
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int rowNumber = 3;
        double imgToSpacingRatio = 2.5;

        //spacing from screen border to image and from image to image equals unit,
        //image widht/height equals 2.5 units
        double unit = width / (imgToSpacingRatio * rowNumber + rowNumber + 1);
        double imgSize =  unit * imgToSpacingRatio;
        double grid_spacing = unit;
        double grid_margin = (width - imgSize * rowNumber - grid_spacing * (rowNumber - 1))/2;

        //grid properties
        imgGrid = (GridView) rootView.findViewById(R.id.img_grid);
        imgGrid.setVerticalSpacing((int)grid_spacing);
        imgGrid.setHorizontalSpacing((int)grid_spacing);
        imgGrid.setColumnWidth((int)imgSize);
        imgGrid.setPadding((int)grid_margin, (int)grid_margin, (int)grid_margin, (int)grid_margin);
        imgGrid.setStretchMode(GridView.STRETCH_SPACING);


        //adapter
        adapter = new ImageAdapter(getContext(), null,(int)imgSize);
        imgGrid.setAdapter(adapter);

    }


}
