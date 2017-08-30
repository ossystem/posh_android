package ru.jufy.myposh.fragments;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.adapters.ImageAdapter;
import ru.jufy.myposh.adapters.ImageGridDecoration;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.data.ImageRepository;
import ru.jufy.myposh.views.ArcLayout;

/**
 * Created by Anna on 4/14/2017.
 */

public class ImageGridFragment extends Fragment {
    protected View rootView;
    protected ImageAdapter adapter;
    protected RecyclerView recyclerView;
    protected List<Object> data;

    protected void setupGrid(List<Object> images, boolean initialSetup) {
        data = images;
        //calculate dimens
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        final int rowNumber = 3;
        double imgToSpacingRatio = 2.5;
        //spacing from screen border to image and from image to image equals unit,
        //image widht/height equals 2.5 units
        double unit = width / (imgToSpacingRatio * rowNumber + rowNumber + 1);
        double imgSize =  unit * imgToSpacingRatio;
        double gridSpacing = unit;

        adapter = new ImageAdapter(getContext(),
                data,
                (int)imgSize, true);

        //grid properties
        recyclerView = (RecyclerView) rootView.findViewById(R.id.image_recycler);
        recyclerView.getItemAnimator().setChangeDuration(0);
        if (initialSetup) {
            GridLayoutManager manager = new GridLayoutManager(getContext(), rowNumber);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch(adapter.getItemViewType(position)){
                        case ImageAdapter.IMAGE:
                            return 1;
                        case ImageAdapter.TEXT:
                            return rowNumber;
                        default:
                            return -1;
                    }
                }
            });
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new ImageGridDecoration(rowNumber, (int) gridSpacing, true));
        }

        recyclerView.setAdapter(adapter);
    }

    @NonNull
    protected static String[] getRequestAuthorized(String url) {
        String[] result = new String[3];
        result[0] = url;
        result[1] = "Authorization";
        StringBuilder token = new StringBuilder("Bearer ");
        token.append(MyPoshApplication.getCurrentToken().getToken());
        result[2] = new String(token);

        return result;
    }

    class ImageClickListener extends ImageAdapter.ClickListener {
        @Override
        public void onSingleClick(View view, int position) {
            Image image = adapter.getImage(position);
            if (null != image) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ImageFragment imgFrag = new ImageFragment();
                imgFrag.setImage(image);
                transaction.replace(R.id.fragment_frame, imgFrag);
                transaction.commit();
            }
        }

        @Override
        public void onDoubleClick(View view, int position) {
            //not supported
        }

        @Override
        public boolean onLongClick(View view, int position) {
            return true;
        }
    }
}
