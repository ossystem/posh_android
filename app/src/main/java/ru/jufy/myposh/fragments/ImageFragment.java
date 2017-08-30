package ru.jufy.myposh.fragments;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    FloatingActionButton fabLikeTrash;
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
        ProgressBar progressBar = (ProgressBar)rootView.findViewById(R.id.bigProgress);
        calculateImageSize();
        image.showMiddle(getActivity(), imageView, progressBar);

        fabCancel = (FloatingActionButton)rootView.findViewById(R.id.fab_cancel);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showCurrentFragment();
            }
        });

        fabLikeTrash = (FloatingActionButton)rootView.findViewById(R.id.fab_like_delete);
        if (image.canUnlike()) {
            setLikedIcon();
        } else if (image.canLike()) {
            setUnlikedIcon();
        } else if (image.canDelete()) {
            setTrashIcon();
        } else {
            fabLikeTrash.setVisibility(View.INVISIBLE);
        }
        fabLikeTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.canUnlike()) {
                    onUnLike();
                } else if (image.canLike()) {
                    onLike();
                } else if (image.canDelete()) {
                    onDelete();
                }
            }
        });

        fabBuyDownload = (FloatingActionButton)rootView.findViewById(R.id.fab_buy_download);
        if (image.canDownload()) {
            setDownloadIcon();
        }
        fabBuyDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.canDownload()) {
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
            fabLikeTrash.setVisibility(View.INVISIBLE);
        }
    }

    private void setLikedIcon() {
        setIcon(fabLikeTrash, R.drawable.icon_liked);
    }

    private void setUnlikedIcon() {
        setIcon(fabLikeTrash, R.drawable.icon_like);
    }

    private void setTrashIcon() {
        setIcon(fabLikeTrash, R.drawable.icon_trash);
    }

    private void setIcon(ImageView view, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(getResources().getDrawable(id, MyPoshApplication.getContext().getTheme()));
        } else {
            view.setImageDrawable(getResources().getDrawable(id));
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

    private void onDelete() {
        if (image.delete()) {
            ((MainActivity)getActivity()).showCurrentFragment();
        } else {
            Toast.makeText(getActivity(), "Не получилось удалить пошик", Toast.LENGTH_LONG).show();
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
