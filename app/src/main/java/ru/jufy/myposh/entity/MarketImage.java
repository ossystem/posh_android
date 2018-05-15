package ru.jufy.myposh.entity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.ui.utils.GlideApp;
import ru.jufy.myposh.ui.utils.HttpDelAsyncTask;
import ru.jufy.myposh.ui.utils.HttpGetAsyncTask;
import ru.jufy.myposh.ui.utils.HttpPostAsyncTask;


/**
 * Created by BorisDev on 04.08.2017.
 */

public class MarketImage extends Image {

    private boolean isFavorite;
    private boolean isPurchased;
    private String link;

    public MarketImage(String id, String extension, boolean isFavorite, boolean isPurchased, String link) {
        super(id, extension);
        this.isFavorite = isFavorite;
        this.isPurchased = isPurchased;
        this.link = link;
    }

    @Override
    public boolean canLike() {
        return !isFavorite && !isPurchased;
    }

    @Override
    public boolean canUnlike() {
        return isFavorite && !isPurchased;
    }

    @Override
    public boolean canDownload() {
        return isPurchased;
    }

    @Override
    public void showSmall(Context context, ImageView view, ProgressBar progressBar) {
/*
        StringBuilder link = getMarketLinkCommonPart();
        link.append("/img?size=small");
*/

        showImage(context, view, progressBar, getMarketLinkCommonPart());
    }

    @NonNull
    private StringBuilder getMarketLinkCommonPart() {
        StringBuilder link = new StringBuilder(this.link);
        /*link.append(id);*/
        return link;
    }

    @Override
    public void showMiddle(Context context, ImageView view, ProgressBar progressBar) {
        StringBuilder link = getMarketLinkCommonPart();
        /*link.append("/img?size=middle");*/

        showImage(context, view, progressBar, link);
    }

    @Override
    public void showBig(Context context, ImageView view, ProgressBar progressBar) {
        StringBuilder link = getMarketLinkCommonPart();
        /*link.append("/img?size=big");*/

        showImage(context, view, progressBar, link);
    }

    private void showImage(Context context, ImageView view, final ProgressBar progressBar, StringBuilder link) {
        try {
            GlideApp
                    .with(context)
                    .load(new URL(link.toString()))
                    .override(size, size)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .apply(RequestOptions.errorOf(R.drawable.error))
                    .into(view);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean like() {
        StringBuilder link = getMarketLinkCommonPart();
        link.append("/fav");
        String imgFavRequest[] = new String[2];
        imgFavRequest[0] = link.toString();
        imgFavRequest[1] = "";
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Authorization", "Bearer " + MyPoshApplication.Companion.getCurrentToken().getToken());
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        postRequest.setRequestProperties(reqProps);
        try {
            String postResult = postRequest.execute(imgFavRequest).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            isFavorite = true;
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean unlike() {
        StringBuilder link = new StringBuilder(MyPoshApplication.Companion.getDOMAIN() +"favorites/");
        link.append(id);
        String imgUnFavRequest[] = new String[2];
        imgUnFavRequest[0] = link.toString();
        imgUnFavRequest[1] = "";
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Authorization", "Bearer " + MyPoshApplication.Companion.getCurrentToken().getToken());
        HttpDelAsyncTask delRequest = new HttpDelAsyncTask();
        delRequest.setRequestProperties(reqProps);
        try {
            String delResult = delRequest.execute(imgUnFavRequest).get();
            if (null == delResult) {
                throw new InterruptedException();
            }
            isFavorite = false;
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean buy() {
        StringBuilder link = getMarketLinkCommonPart();
        String imgBuyRequest[] = new String[2];
        imgBuyRequest[0] = link.toString();
        imgBuyRequest[1] = "";
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Authorization", "Bearer " + MyPoshApplication.Companion.getCurrentToken().getToken());
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        postRequest.setRequestProperties(reqProps);
        try {
            String postResult = postRequest.execute(imgBuyRequest).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            isPurchased = true;
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean download() {
        StringBuilder link = new StringBuilder(MyPoshApplication.Companion.getDOMAIN() + "poshiks/purchase/set/");
        link.append(id);

        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        try {
            tempFile = createTempFile();
            getRequest.setFileToStoreImage(tempFile);
            String getResult = getRequest.execute(getRequestAuthorized(link.toString())).get();
            if (null == getResult) {
                tempFile.delete();
                throw new InterruptedException();
            }

            if (getRequest.receivedDataIsBinary()) {
                return true;
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getTempFilename() {
        return "market_" + id +
                "." +
                extension;
    }
}
