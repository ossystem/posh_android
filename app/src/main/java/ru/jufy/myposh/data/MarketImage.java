package ru.jufy.myposh.data;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.GlideApp;


/**
 * Created by BorisDev on 04.08.2017.
 */

public class MarketImage extends Image {

    @Override
    public void showSmall(Context context, ImageView view) {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/market/");
        link.append(id);
        link.append("/img?size=small");

        showImage(context, view, link);
    }

    @Override
    public void showMiddle(Context context, ImageView view) {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/market/");
        link.append(id);
        link.append("/img?size=middle");

        showImage(context, view, link);
    }

    @Override
    public void showBig(Context context, ImageView view) {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/market/");
        link.append(id);
        link.append("/img?size=big");

        showImage(context, view, link);
    }

    private void showImage(Context context, ImageView view, StringBuilder link) {
        GlideApp
                .with(context)
                .load(link.toString())
                .override(size, size)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .apply(RequestOptions.placeholderOf(R.drawable.pink))
                .apply(RequestOptions.errorOf(R.drawable.error))
                .into(view);
    }
}
