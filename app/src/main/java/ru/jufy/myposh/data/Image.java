package ru.jufy.myposh.data;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;

import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.GlideApp;

/**
 * Created by Anna on 4/18/2017.
 */

public class Image {
    protected int id;
    protected int size;

    public Image(int id) {
        this.id = id;
    }

    public boolean canLike() {
        return false;
    }

    public boolean canUnlike() {
        return false;
    }

    public boolean canDownload() {
        return true;
    }

    public boolean isMe(Image imgToCompare) {
        return imgToCompare.id == id;
    }

    public void setSize(int size) {
        this.size = (int)(size * 0.8);
    }

    public void showSmall(Context context, ImageView view) {
        show(context, view);
    }

    public void showMiddle(Context context, ImageView view) {
        show(context, view);
    }

    public void showBig(Context context, ImageView view) {
        show(context, view);
    }

    private void show(Context context, ImageView view) {
        GlideApp.with(context)
                .load(R.drawable.pink)
                .circleCrop()
                .apply(RequestOptions.placeholderOf(R.drawable.pink))
                .apply(RequestOptions.errorOf(R.drawable.error))
                .into(view);
    }

    public boolean like() {
        return false;
    }

    public boolean unlike() {
        return false;
    }

    public boolean buy() {
        return false;
    }
}
