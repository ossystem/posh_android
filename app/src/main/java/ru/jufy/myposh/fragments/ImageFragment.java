package ru.jufy.myposh.fragments;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.activities.MainActivity;
import ru.jufy.myposh.data.Image;

/**
 * Created by BorisDev on 07.08.2017.
 */

public class ImageFragment extends Fragment {
    private View rootView;
    FloatingActionButton fabCancel;
    FloatingActionButton fabLike;
    FloatingActionButton fabBuyDownload;
    private Image image;

    public ImageFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.bigImage);
        calculateImageSize();
        image.showMiddle(getActivity(), imageView);
        fabCancel = (FloatingActionButton)rootView.findViewById(R.id.fab_cancel);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showCurrentFragment();
            }
        });
        fabLike = (FloatingActionButton)rootView.findViewById(R.id.fab_like);
        if (image.isFavorite) {
            setLikedIcon();
        } else {
            setUnlikedIcon();
        }
        fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.isFavorite) {
                    onUnLike();
                } else {
                    onLike();
                }
            }
        });

        fabBuyDownload = (FloatingActionButton)rootView.findViewById(R.id.fab_buy_download);
        if (image.isPurchased) {
            fabLike.setVisibility(View.INVISIBLE);
            setDownloadIcon();
        }
        fabBuyDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.isPurchased) {
                    downloadImage();
                } else {
                    buyImage();
                }
            }
        });

        ((MainActivity)getActivity()).hideBottomNav();

        return rootView;
    }

    private void setDownloadIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabBuyDownload.setImageDrawable(getResources().getDrawable(R.drawable.icon_install, MyPoshApplication.getContext().getTheme()));
        } else {
            fabBuyDownload.setImageDrawable(getResources().getDrawable(R.drawable.icon_install));
        }
    }

    private void downloadImage() {

    }

    private void buyImage() {
        if (image.buy()) {
            setDownloadIcon();
            fabLike.setVisibility(View.INVISIBLE);
        }
    }

    private void setLikedIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabLike.setImageDrawable(getResources().getDrawable(R.drawable.icon_liked, MyPoshApplication.getContext().getTheme()));
        } else {
            fabLike.setImageDrawable(getResources().getDrawable(R.drawable.icon_liked));
        }
    }

    private void setUnlikedIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabLike.setImageDrawable(getResources().getDrawable(R.drawable.icon_like, MyPoshApplication.getContext().getTheme()));
        } else {
            fabLike.setImageDrawable(getResources().getDrawable(R.drawable.icon_like));
        }
    }

    private void onUnLike() {
        if (image.unlike()) {
            setUnlikedIcon();
        }
    }

    private void onLike() {
        if (image.like()) {
            setLikedIcon();
        }
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private void calculateImageSize() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.image.setSize(size.x);
    }
}
