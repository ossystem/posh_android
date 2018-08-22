package ru.jufy.myposh.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class DataLayer {
    private static String TAG = "DataLayer";

    private static Asset toAsset(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public static void sendImage(Context context, Bitmap bitmap) {
        Asset asset = toAsset(bitmap);

        sendImage(context, "/image", asset);
    }

    public static void sendImageBinary(Context context, byte[] data) {
        Asset asset = Asset.createFromBytes(data);

        sendImage(context, "/imageBinary", asset);
    }

    public static void sendImageURL(Context context, String data) {
        Asset asset = Asset.createFromBytes(data.getBytes());

        sendImage(context, "/imageURL", asset);
    }

    public static void sendImage(Context context, String type, Asset asset) {
        Log.d(TAG, "sending image with type: " + type);

        PutDataMapRequest dataMap = PutDataMapRequest.create(type);

        dataMap.getDataMap().putAsset("image", asset);
        dataMap.getDataMap().putLong("time", new Date().getTime());

        PutDataRequest request = dataMap.asPutDataRequest();

        request.setUrgent();

        Task<DataItem> dataItemTask = Wearable.getDataClient(context).putDataItem(request);

        dataItemTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d(TAG, "Sending image was successful: " + dataItem);
                    }
                });
    }
}
