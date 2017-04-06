package ru.jufy.myposh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.Telephony;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mikhaellopez.circularimageview.CircularImageView;

import ru.jufy.myposh.data.ImageRepository;

/**
 * Created by Anna on 4/5/2017.
 */

public class ImageAdapter extends BaseAdapter {

    private ImageRepository data;
    private Context context;
    int imgSize;

    public ImageAdapter(Context context, ImageRepository data, int imgSize) {
        this.context = context;
        this.data = data;
        this.imgSize = imgSize;
    }

    @Override
    public int getCount() {
        return 21;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        CircularImageView imageView;
        if (convertView == null) {
            imageView = new CircularImageView(context);
        }
        else {
            imageView = (CircularImageView) convertView;
        }
        imageView.setLayoutParams(new GridView.LayoutParams(imgSize,imgSize));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBorderWidth(0);
        imageView.setShadowRadius(0);
        imageView.setImageResource(R.drawable.pink);
        return imageView;
    }

}
