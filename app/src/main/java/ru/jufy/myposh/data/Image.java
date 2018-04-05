package ru.jufy.myposh.data;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.GlideApp;

/**
 * Created by Anna on 4/18/2017.
 */

public abstract class Image {
    protected int id;
    protected int size;
    protected String extension;

    protected File tempFile = null;

    public Image(int id, String extension) {
        this.id = id;
        this.extension = extension;
    }

    /*public boolean available() {
        File file = new File(getCacheFolder(), getTempFilename());
        if(file.exists()) {
            tempFile = file;
            return true;
        } else {
            return false;
        }
    }*/

    public boolean canLike() {
        return false;
    }

    public boolean canUnlike() {
        return false;
    }

    public boolean canDownload() {
        return true;
    }

    public boolean canDelete() {
        return false;
    }

    public boolean isMe(Image imgToCompare) {
        return imgToCompare.id == id;
    }

    public void setSize(int size) {
        this.size = (int)(size * 0.8);
    }

    public void showSmall(Context context, ImageView view, ProgressBar progressBar) {
        show(context, view);
    }

    public void showMiddle(Context context, ImageView view, ProgressBar progressBar) {
        show(context, view);
    }

    public void showBig(Context context, ImageView view, ProgressBar progressBar) {
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

    public boolean delete() {
        return false;
    }

    public boolean download() {
        return false;
    }

    public File getDownloadedFile() {return tempFile;}

    @NonNull
    protected static String[] getRequestAuthorized(String url) {
        String[] result = new String[3];
        result[0] = url;
        result[1] = "Authorization";
        StringBuilder token = new StringBuilder("Bearer ");
        token.append(MyPoshApplication.getCurrentToken().getToken());
        result[2] = new String(token);

        return result;
    }

    @NonNull
    protected File createTempFile() {
        File cacheDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);//MyPoshApplication.getContext().getCacheDir();
        //File.createTempFile(filename.toString(), "jpeg", cacheDir);
        //return new File(getCacheFolder(), getTempFilename());
        return new File(cacheDir, getTempFilename());
    }

    /*protected File getCacheFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }*/

    public abstract String getTempFilename();
}
