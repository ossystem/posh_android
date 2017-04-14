package ru.jufy.myposh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.mikhaellopez.circularimageview.CircularImageView;

import ru.jufy.myposh.R;
import ru.jufy.myposh.data.ImageRepository;

/**
 * Created by Anna on 4/14/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder>{

    private ImageRepository data;
    private Context context;
    int imgSize;

    public ImageAdapter(Context context, ImageRepository data, int imgSize) {
        this.context = context;
        this.data = data;
        this.imgSize = imgSize;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CircularImageView imageView;
        imageView = new CircularImageView(context);
        imageView.setLayoutParams(new RecyclerView.LayoutParams(imgSize,imgSize));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBorderWidth(0);
        imageView.setShadowRadius(0);
        imageView.setImageResource(R.drawable.pink);
        return new ImageHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        holder.imageView.setImageResource(R.drawable.pink);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView;
        }
    }
}
