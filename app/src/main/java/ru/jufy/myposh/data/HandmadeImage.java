package ru.jufy.myposh.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.GlideApp;
import ru.jufy.myposh.utils.HttpDelAsyncTask;
import ru.jufy.myposh.utils.HttpGetAsyncTask;

/**
 * Created by BorisDev on 28.08.2017.
 */

public class HandmadeImage extends Image {

    public HandmadeImage(int id, String extension) {
        super(id, extension);
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public boolean delete() {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/poshiks/my/");
        link.append(id);
        String imgDelRequest[] = new String[2];
        imgDelRequest[0] = link.toString();
        imgDelRequest[1] = "";
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Authorization", "Bearer " + MyPoshApplication.getCurrentToken().getToken());
        HttpDelAsyncTask delRequest = new HttpDelAsyncTask();
        delRequest.setRequestProperties(reqProps);
        try {
            String delResult = delRequest.execute(imgDelRequest).get();
            if (null == delResult) {
                throw new InterruptedException();
            }
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void showSmall(Context context, ImageView view, ProgressBar progressBar) {
        StringBuilder link = getHandmadeLinkCommonPart();
        link.append("/img?size=small");

        showImage(context, view, progressBar, link);
    }

    private StringBuilder getHandmadeLinkCommonPart() {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/poshiks/my/");
        link.append(id);
        return link;
    }

    @Override
    public void showMiddle(Context context, ImageView view, ProgressBar progressBar) {
        StringBuilder link = getHandmadeLinkCommonPart();
        link.append("/img?size=middle");

        showImage(context, view, progressBar, link);
    }

    @Override
    public void showBig(Context context, ImageView view, ProgressBar progressBar) {
        StringBuilder link = getHandmadeLinkCommonPart();
        link.append("/img?size=big");

        showImage(context, view, progressBar, link);
    }

    private void showImage(Context context, ImageView view, final ProgressBar progressBar, StringBuilder link) {
        GlideApp
                .with(context)
                .load(link.toString())
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
                //.apply(RequestOptions.placeholderOf(R.drawable.pink))
                .apply(RequestOptions.errorOf(R.drawable.error))
                .into(view);
    }

    @Override
    public boolean download() {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/poshiks/my/set/");
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
    protected String getTempFilename() {
        StringBuilder filename = new StringBuilder("hm_");
        filename.append(Integer.toString(id));
        filename.append(".");
        filename.append(extension);

        return filename.toString();
    }
}
