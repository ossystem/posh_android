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
    public void show(Context context, ImageView view) {
        StringBuilder linkBuilder = new StringBuilder("http://kulon.jwma.ru/api/v1/market/");
        linkBuilder.append(id);
        linkBuilder.append("/img?size=small");

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor()).build();
        Picasso image = new Picasso.Builder(context).downloader(new OkHttp3Downloader(okHttpClient)).build();
        image.load(linkBuilder.toString())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.pink)
                .into(view);
    }
}
