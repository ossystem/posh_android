package ru.jufy.myposh.data;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.GlideApp;
import ru.jufy.myposh.utils.HttpDelAsyncTask;
import ru.jufy.myposh.utils.HttpPostAsyncTask;


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

    @Override
    public boolean like() {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/market/");
        link.append(id);
        link.append("/fav");
        String imgFavRequest[] = new String[2];
        imgFavRequest[0] = link.toString();
        imgFavRequest[1] = "";
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Authorization", "Bearer " + MyPoshApplication.getCurrentToken().getToken());
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        postRequest.setRequestProperties(reqProps);
        try {
            String postResult = postRequest.execute(imgFavRequest).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            isFavorite = true;
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean unlike() {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/favorites/");
        link.append(id);
        String imgUnFavRequest[] = new String[2];
        imgUnFavRequest[0] = link.toString();
        imgUnFavRequest[1] = "";
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Authorization", "Bearer " + MyPoshApplication.getCurrentToken().getToken());
        HttpDelAsyncTask delRequest = new HttpDelAsyncTask();
        delRequest.setRequestProperties(reqProps);
        try {
            String delResult = delRequest.execute(imgUnFavRequest).get();
            if (null == delResult) {
                throw new InterruptedException();
            }
            isFavorite = false;
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean buy() {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/market/");
        link.append(id);
        String imgBuyRequest[] = new String[2];
        imgBuyRequest[0] = link.toString();
        imgBuyRequest[1] = "";
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Authorization", "Bearer " + MyPoshApplication.getCurrentToken().getToken());
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        postRequest.setRequestProperties(reqProps);
        try {
            String postResult = postRequest.execute(imgBuyRequest).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            isPurchased = true;
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
