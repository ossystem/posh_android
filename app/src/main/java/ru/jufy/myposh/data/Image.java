package ru.jufy.myposh.data;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ru.jufy.myposh.R;

/**
 * Created by Anna on 4/18/2017.
 */

public class Image {
    public int id;
    public boolean isFavorite;
    public boolean isPurchased;

    public void showSmall(Context context, ImageView view) {
        show(context, view);
    }

    public void showBig(Context context, ImageView view) {
        show(context, view);
    }

    private void show(Context context, ImageView view) {
        Picasso.with(context)
                .load(R.drawable.pink)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.pink)
                .into(view);
    }
}
