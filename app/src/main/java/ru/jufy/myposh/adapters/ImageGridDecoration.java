package ru.jufy.myposh.adapters;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Anna on 4/14/2017.
 */

public class ImageGridDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;


    public ImageGridDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;

        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        ImageAdapter adapter = (ImageAdapter) parent.getAdapter();
        int position = parent.getChildAdapterPosition(view); // item position
        int column = adapter.getItemColumn(position, spanCount);
        boolean isFirstRow = adapter.isFirstRow(position, spanCount);

        if (includeEdge) {

            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (isFirstRow) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (!isFirstRow) {
                outRect.top = spacing; // item top
            }
        }

    }
}