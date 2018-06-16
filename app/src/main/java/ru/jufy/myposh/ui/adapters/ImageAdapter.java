package ru.jufy.myposh.ui.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.jufy.myposh.R;
import ru.jufy.myposh.entity.Image;
import ru.jufy.myposh.entity.MarketImage;

/**
 * Created by Anna on 4/14/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> data;
    private Context context;
    private int imgSize;
    private ClickListener clickListener;
    private boolean supportsDoubleClick;
    private List<Boolean> selected;
    public static final int IMAGE = 0, TEXT = 1;

    public boolean isDoubleClickSupported() {
        return supportsDoubleClick;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof Image) {
            return IMAGE;
        } else if (data.get(position) instanceof String) {
            return TEXT;
        }
        return -1;
    }

    public MarketImage getImage(final int position) {
        return (MarketImage) data.get(position);
    }

    public void setSupportsDoubleClick(boolean supportsDoubleClick) {
        this.supportsDoubleClick = supportsDoubleClick;
        if (clickListener != null) {
            clickListener.supportsDoubleClick = supportsDoubleClick;
        }
    }

    public ImageAdapter(Context context, List<Object> data, int imgSize, boolean supportsDoubleClick) {
        this.context = context;
        this.data = data;
        this.selected = new ArrayList<>(Collections.nCopies(data.size(), false));
        this.imgSize = imgSize;
        this.supportsDoubleClick = supportsDoubleClick;
    }

    public ImageAdapter(Context context, int imgSize, boolean supportsDoubleClick) {
        this.context = context;
        this.imgSize = imgSize;
        this.data = new ArrayList<>();
        this.selected = new ArrayList<>();
        this.supportsDoubleClick = supportsDoubleClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View v;
        switch (viewType) {
            case IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.image_item, parent, false);
                v.setLayoutParams(new RecyclerView.LayoutParams(imgSize, imgSize));
                holder = new ImageHolder(v);
                int dltSize = (int) (imgSize * 0.2);
                RelativeLayout.LayoutParams dltlp = (RelativeLayout.LayoutParams)
                        ((ImageHolder) holder).overlayDelete.getLayoutParams();
                dltlp.height = dltlp.width = dltSize;
                dltlp.topMargin = dltlp.leftMargin = (int) (imgSize / 2 * (1 - 1 / Math.sqrt(2) - 0.2));
                ((ImageHolder) holder).overlayFavorite.setVisibility(View.GONE);
                return holder;
            case TEXT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);
                holder = new TextHolder(v);
                return holder;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case IMAGE:
                ImageHolder image_vh = (ImageHolder)holder;
                ((Image)data.get(position)).setSize(imgSize);
                ((Image)data.get(position)).showSmall(context, image_vh.imageView, image_vh.progressBar);

                if (clickListener != null) {
                    image_vh.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickListener.onClick(view, position);
                        }
                    });
                    image_vh.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            return clickListener.onLongClick(view, position);
                        }
                    });
                }
                if (selected.get(position)) {
                    image_vh.overlayDelete.setVisibility(View.VISIBLE);
                } else {
                    image_vh.overlayDelete.setVisibility(View.GONE);
                }
                break;
            case TEXT:
                TextHolder text_vh = (TextHolder)holder;
                text_vh.textView.setText((String)data.get(position));
                break;
        }
    }

    public void setClickListener(ClickListener listener) {
        clickListener = listener;
        clickListener.supportsDoubleClick = supportsDoubleClick;
    }


    public void setSelected(int position, boolean selected) {
        if (getItemViewType(position) == ImageAdapter.IMAGE) {
            this.selected.set(position, selected);
            notifyDataSetChanged();
        }
    }

    public boolean isSelected(int position) {
        return selected.get(position);
    }

    public boolean isAnySelected() {
        return selected.contains(true);
    }


    public void setSelectedAll(boolean selected) {
        for (int i = 0; i < this.selected.size(); i++) {
            if (data.get(i) instanceof Image) {
                this.selected.set(i, selected);
            }
        }
        notifyDataSetChanged();
    }


    public List<Image> getSelectedImages() {
        List<Image> selected = new ArrayList<>();
        for (int i = 0; i < this.selected.size(); i++) {
            if (this.selected.get(i)) {
                selected.add((Image)data.get(i));
            }
        }
        return selected;
    }

    public void setData(List<Object> data) {
        this.data = data;
        this.selected = new ArrayList<>(Collections.nCopies(data.size(), false));
        notifyDataSetChanged();
    }

    public void add(Object item) {
        this.data.add(item);
        this.selected.add(false);
        notifyItemInserted(this.data.size() - 1);
    }

    public void addAll(List<Object> items) {
        for (Object item : items) {
            add(item);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    int getItemColumn(int position, int columnsNumber) {
        int result = 0;
        boolean firstImgAfterText = true;
        for (int i = 0; i <= position; ++i) {
            switch (getItemViewType(i)) {
                case IMAGE:
                    if (firstImgAfterText) {
                        firstImgAfterText = false;
                        continue;
                    }
                    ++result;
                    if (result == columnsNumber) {
                        result = 0;
                    }
                    break;
                default:
                    result = 0;
                    firstImgAfterText = true;
                    break;
            }
        }

        return result;
    }

    boolean isFirstRow(int position, int columnsNumber) {
        int column = 0;
        for (int i = 0; i < position; ++i) {
            switch (getItemViewType(i)) {
                case IMAGE:
                    ++column;
                    if (column == columnsNumber) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
        View overlayDelete;
        View overlayFavorite;
        ImageHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.image);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
            overlayDelete = itemView.findViewById(R.id.overlay_delete);
            overlayFavorite = itemView.findViewById(R.id.overlay_favorite);
        }
    }

    private class TextHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView;
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
