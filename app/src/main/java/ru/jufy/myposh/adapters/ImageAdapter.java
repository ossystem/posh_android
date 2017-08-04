package ru.jufy.myposh.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.jufy.myposh.R;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.data.ImageRepository;

/**
 * Created by Anna on 4/14/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder>{

    private List<Image> data;
    private Context context;
    int imgSize;
    private ClickListener clickListener;
    private boolean supportsDoubleClick;
    private List<Boolean> selected;


    public boolean isDoubleClickSupported() {
        return supportsDoubleClick;
    }

    public void setSupportsDoubleClick(boolean supportsDoubleClick) {
        this.supportsDoubleClick = supportsDoubleClick;
        if (clickListener != null) {
            clickListener.supportsDoubleClick = supportsDoubleClick;
        }
    }

    public ImageAdapter(Context context, List<Image> data, int imgSize, boolean supportsDoubleClick) {
        this.context = context;
        this.data = data;
        this.selected = new ArrayList<>(Collections.nCopies(data.size(), false));
        this.imgSize = imgSize;
        this.supportsDoubleClick = supportsDoubleClick;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false);
        v.setLayoutParams(new RecyclerView.LayoutParams(imgSize,imgSize));
        ImageHolder holder = new ImageHolder(v);
        int dltSize = (int)(imgSize * 0.2);
        RelativeLayout.LayoutParams dltlp = (RelativeLayout.LayoutParams)
                holder.overlayDelete.getLayoutParams();
        dltlp.height = dltlp.width =  dltSize;
        dltlp.topMargin = dltlp.leftMargin = (int)(imgSize/2 * (1 - 1/Math.sqrt(2) - 0.2));
        holder.overlayFavorite.setVisibility(View.GONE);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, final int position) {

        data.get(position).show(context, holder.imageView);

        if (clickListener != null) {
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(view, position);
                }
            });
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return clickListener.onLongClick(view, position);
                }
            });
        }
        if (selected.get(position)) {
            holder.overlayDelete.setVisibility(View.VISIBLE);
        }
        else {
            holder.overlayDelete.setVisibility(View.GONE);
        }
    }

    public void setClickListener(ClickListener listener) {
        clickListener = listener;
        clickListener.supportsDoubleClick = supportsDoubleClick;
    }


    public void setSelected(int position, boolean selected) {
        this.selected.set(position, selected);
        notifyDataSetChanged();
    }

    public boolean isSelected(int position) {
        return selected.get(position);
    }

    public boolean isAnySelected() {
        return selected.contains(true);
    }


    public void setSelectedAll(boolean selected) {
        for (int i = 0; i < this.selected.size(); i++) {
            this.selected.set(i, selected);
        }
        notifyDataSetChanged();
    }


    public List<Image> getSelected() {
        List<Image> selected = new ArrayList<>();
        for (int i = 0; i < this.selected.size(); i++) {
            if (this.selected.get(i)) {
                selected.add(data.get(i));
            }
        }
        return selected;
    }

    public void setData(List<Image> data) {
        this.data = data;
        this.selected = new ArrayList<>(Collections.nCopies(data.size(), false));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View overlayDelete;
        View overlayFavorite;
        public ImageHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.image);
            overlayDelete = itemView.findViewById(R.id.overlay_delete);
            overlayFavorite = itemView.findViewById(R.id.overlay_favorite);
        }
    }

    public abstract static class ClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds
        private boolean supportsDoubleClick;
        long previousPosition = -1;

        Handler handler = new Handler();
        Runnable runnable;

        private final void onClickDoubleSupported(final View view, final int position) {
            if (runnable != null && position == previousPosition) {
                //Double click
                //Remove single click runnable
                handler.removeCallbacks(runnable);
                runnable = null;
                onDoubleClick(view, position);
            }
            else {
                //Previous click was too long ago or user clicked different image
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        onSingleClick(view, position);
                        runnable = null;
                    }
                };
                handler.postDelayed(runnable, DOUBLE_CLICK_TIME_DELTA);
            }
            previousPosition = position;
        }

        private final void onCickDoubleNotSupported(final View view, final int position) {
            onSingleClick(view, position);
        }

        private final void onClick(final View view, final int position) {
            if (supportsDoubleClick) {
                onClickDoubleSupported(view, position);
            }
            else {
                onCickDoubleNotSupported(view,position);
            }
        }


        public abstract void onSingleClick(View view, int position);
        public abstract void onDoubleClick(View view, int position);
        public abstract boolean onLongClick(View view, int position);

    }



}
