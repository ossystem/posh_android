package ru.jufy.myposh.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.activities.MainActivity;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.utils.HttpDelAsyncTask;
import ru.jufy.myposh.utils.HttpPostAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;

import static android.R.attr.id;

/**
 * Created by BorisDev on 07.08.2017.
 */

public class ImageFragment extends Fragment {
    private View rootView;
    FloatingActionButton fabCancel;
    FloatingActionButton fabLike;
    private Image image;

    public ImageFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.bigImage);
        image.showBig(getActivity(), imageView);
        fabCancel = (FloatingActionButton)rootView.findViewById(R.id.fab_cancel);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showMarket();
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
        ((MainActivity)getActivity()).hideBottomNav();
        return rootView;
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
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/favorites/");
        link.append(image.id);
        String imgUnFavRequest[] = new String[4];
        imgUnFavRequest[0] = link.toString();
        imgUnFavRequest[1] = "";
        imgUnFavRequest[2] = "Authorization";
        imgUnFavRequest[3] = "Bearer " + MyPoshApplication.getCurrentToken().getToken();
        HttpDelAsyncTask delRequest = new HttpDelAsyncTask();
        try {
            String delResult = delRequest.execute(imgUnFavRequest).get();
            if (null == delResult) {
                throw new InterruptedException();
            }
            setUnlikedIcon();
            image.isFavorite = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void onLike() {
        StringBuilder link = new StringBuilder("http://kulon.jwma.ru/api/v1/market/");
        link.append(image.id);
        link.append("/fav");
        String imgFavRequest[] = new String[4];
        imgFavRequest[0] = link.toString();
        imgFavRequest[1] = "";
        imgFavRequest[2] = "Authorization";
        imgFavRequest[3] = "Bearer " + MyPoshApplication.getCurrentToken().getToken();
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        try {
            String postResult = postRequest.execute(imgFavRequest).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            setLikedIcon();
            image.isFavorite = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
