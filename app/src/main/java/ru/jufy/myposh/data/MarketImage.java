package ru.jufy.myposh.data;

import android.content.Context;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.AuthInterceptor;


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
    public void showBig(Context context, ImageView view) {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/market/");
        link.append(id);
        link.append("/img?size=big");

        showImage(context, view, link);
    }

    private void showImage(Context context, ImageView view, StringBuilder link) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor()).build();
        Picasso image = new Picasso.Builder(context).downloader(new OkHttp3Downloader(okHttpClient)).build();
        image.load(link.toString())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.pink)
                .into(view);
    }
}
