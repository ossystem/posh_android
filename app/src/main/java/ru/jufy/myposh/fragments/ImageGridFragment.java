package ru.jufy.myposh.fragments;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;

import ru.jufy.myposh.R;
import ru.jufy.myposh.adapters.ImageAdapter;
import ru.jufy.myposh.adapters.ImageGridDecoration;
import ru.jufy.myposh.data.ImageRepository;
import ru.jufy.myposh.views.ArcLayout;

/**
 * Created by Anna on 4/14/2017.
 */

public class ImageGridFragment extends Fragment {
    protected View rootView;
    protected ImageAdapter adapter;
    protected RecyclerView imageGrid;

    protected void setupGrid(ImageRepository imageRepository) {
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
        double gridSpacing = unit;
        double gridMargin = (width - imgSize * rowNumber - gridSpacing * (rowNumber - 1))/2;

        //grid properties
        int marginTop = ((ArcLayout)rootView.findViewById(R.id.arc_layout)).getPreferredHeight();
        imageGrid = (RecyclerView) rootView.findViewById(R.id.image_recycler);
        GridLayoutManager manager = new GridLayoutManager(getContext(), rowNumber);
        imageGrid.setLayoutManager(manager);
        imageGrid.addItemDecoration(new ImageGridDecoration(rowNumber, (int) gridSpacing, true));
        adapter = new ImageAdapter(getContext(), null,(int)imgSize);
        imageGrid.setAdapter(adapter);

    }
}
