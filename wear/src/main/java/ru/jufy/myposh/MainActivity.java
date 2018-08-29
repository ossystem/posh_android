package ru.jufy.myposh;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity
        implements
            DataClient.OnDataChangedListener,
            MessageClient.OnMessageReceivedListener {

    private static final String TAG = "MainActivity";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        // Enables Always-on
        setAmbientEnabled();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Wearable.getDataClient(this).addListener(this);
        Wearable.getMessageClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged(): " + dataEvents);

        for (DataEvent event : dataEvents) {
            if (event.getType() != DataEvent.TYPE_CHANGED) {
                continue;
            }

            String path = event.getDataItem().getUri().getPath();
            if (path.equals("/image")) {
                Log.w(TAG, "not implemented");
            } else if (path.equals("/imageBinary")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset photoAsset = dataMapItem.getDataMap().getAsset("image");

                new LoadImageFileAsyncTask().execute(photoAsset);
            } else if (path.equals("/imageURL")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset photoAsset = dataMapItem.getDataMap().getAsset("image");

                new LoadImageURLAsyncTask().execute(photoAsset);
            } else {
                Log.d(TAG, "Unrecognized path: " + path);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Log.d(TAG, "onMessageReceived: " + event);
    }

    private class LoadImageURLAsyncTask extends AsyncTask<Asset, Void, String> {
        @Override
        protected String doInBackground(Asset... params) {
            if (params.length > 0) {
                Asset asset = params[0];

                if (asset == null) {
                    Log.e(TAG, "Asset is null.");
                    return null;
                }

                Task<DataClient.GetFdForAssetResponse> getFdForAssetResponseTask =
                        Wearable.getDataClient(getApplicationContext()).getFdForAsset(asset);

                try {
                    DataClient.GetFdForAssetResponse getFdForAssetResponse =
                            Tasks.await(getFdForAssetResponseTask);

                    InputStream assetInputStream = getFdForAssetResponse.getInputStream();

                    if (assetInputStream != null) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(assetInputStream));
                        String data = br.readLine();

                        return data;
                    } else {
                        Log.w(TAG, "Requested an unknown Asset.");
                        return null;
                    }

                } catch (ExecutionException exception) {
                    Log.e(TAG, "Failed retrieving asset, Task failed: " + exception);
                    return null;

                } catch (InterruptedException exception) {
                    Log.e(TAG, "Failed retrieving asset, interrupt occurred: " + exception);
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e(TAG, "Asset must be non-null");
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String image) {
            if (image == null) {
                Log.d(TAG, "argument is null. Cannot set it as background");
                return;
            }

            GlideApp.with(getApplicationContext())
                    .load(image)
                    .into(imageView);
        }
    }

    private class LoadImageFileAsyncTask extends AsyncTask<Asset, Void, File> {
        @Override
        protected File doInBackground(Asset... params) {
            if (params.length > 0) {
                Asset asset = params[0];

                if (asset == null) {
                    Log.e(TAG, "Asset is null.");
                    return null;
                }

                Task<DataClient.GetFdForAssetResponse> getFdForAssetResponseTask =
                        Wearable.getDataClient(getApplicationContext()).getFdForAsset(asset);

                try {
                    DataClient.GetFdForAssetResponse getFdForAssetResponse =
                            Tasks.await(getFdForAssetResponseTask);

                    InputStream assetInputStream = getFdForAssetResponse.getInputStream();

                    if (assetInputStream != null) {
                        File file = File.createTempFile("image", ".posh", getCacheDir());
                        OutputStream output = new FileOutputStream(file);
                        try {
                            byte[] buffer = new byte[4 * 1024];
                            int read;

                            while ((read = assetInputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }

                            output.flush();
                        } finally {
                            output.close();
                        }

                        return file;
                    } else {
                        Log.w(TAG, "Requested an unknown Asset.");
                        return null;
                    }

                } catch (ExecutionException exception) {
                    Log.e(TAG, "Failed retrieving asset, Task failed: " + exception);
                    return null;

                } catch (InterruptedException exception) {
                    Log.e(TAG, "Failed retrieving asset, interrupt occurred: " + exception);
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e(TAG, "Asset must be non-null");
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(File image) {
            if (image == null) {
                Log.d(TAG, "argument is null. Cannot set it as background");
                return;
            }

            GlideApp.with(getApplicationContext())
                    .load(image)
                    .into(imageView);
        }
    }
}
