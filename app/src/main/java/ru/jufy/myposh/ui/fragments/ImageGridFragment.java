package ru.jufy.myposh.ui.fragments;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;

import com.jufy.mgtshr.ui.base.BaseFragment;

import java.util.List;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.entity.MarketImage;
import ru.jufy.myposh.ui.adapters.ImageAdapter;
import ru.jufy.myposh.ui.adapters.ImageGridDecoration;

/**
 * Created by Anna on 4/14/2017.
 */

public abstract class ImageGridFragment extends BaseFragment {
    protected View rootView;
    protected ImageAdapter adapter;
    protected RecyclerView recyclerView;
    protected List<Object> data;
    protected ArtworkListener listener;
    private int lastDisplayedPage;
    private int totalNumPages;

    protected void setupGrid(List<Object> images, boolean initialSetup) {
        lastDisplayedPage = 1;
        totalNumPages = 1;

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

        adapter = new ImageAdapter(getContext(),
                data,
                (int)imgSize, true);

        //grid properties
        recyclerView = rootView.findViewById(R.id.image_recycler);
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
            recyclerView.addItemDecoration(new ImageGridDecoration(rowNumber, (int) unit, true));
        }

        recyclerView.setAdapter(adapter);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager layoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    loadMore();
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
    }

    protected void setupGrid() {
        lastDisplayedPage = 1;
        totalNumPages = 1;

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

        adapter = new ImageAdapter(getContext(), (int)imgSize, true);

        //grid properties
        recyclerView = rootView.findViewById(R.id.image_recycler);
        recyclerView.getItemAnimator().setChangeDuration(0);

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
            recyclerView.addItemDecoration(new ImageGridDecoration(rowNumber, (int) unit, true));

        recyclerView.setAdapter(adapter);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager layoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    loadMore();
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
    }


    private void loadMore() {
        if (lastDisplayedPage < totalNumPages) {
            List<Object> poshiksList = getAllPoshiksAtPage(++lastDisplayedPage);
            adapter.addAll(poshiksList);
        }
    }

    protected abstract List<Object> getAllPoshiksAtPage(int page);

    protected void setTotalPagesNum(int value) {
        totalNumPages = value;
    }

    protected void resetLastDisplayedPage() {
        lastDisplayedPage = 1;
    }

    @NonNull
    protected static String[] getRequestAuthorized(String url) {
        String[] result = new String[3];
        result[0] = url;
        result[1] = "Authorization";
        StringBuilder token = new StringBuilder("Bearer ");
        token.append(MyPoshApplication.Companion.getCurrentToken().getToken());
        result[2] = new String(token);

        return result;
    }

    public class ImageClickListener extends ImageAdapter.ClickListener {
        @Override
        public void onSingleClick(View view, int position) {
            MarketImage image = adapter.getImage(position);
            if (image !=null && listener!=null) {
                listener.artworkClicked(image);
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

    public interface ArtworkListener{
        void artworkClicked(MarketImage image);
    }
}
