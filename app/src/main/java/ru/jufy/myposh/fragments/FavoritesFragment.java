package ru.jufy.myposh.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.adapters.ImageAdapter;
import ru.jufy.myposh.R;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.utils.HttpGetAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;


public class FavoritesFragment extends ImageGridFragment {

    private FloatingActionButton cancelFab;
    private FloatingActionButton deleteFab;
    private FloatingActionButton selectAllFab;


    public FavoritesFragment() {
        // Required empty public constructor
    }


    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        List<Object> favoritesList = getFavorites();
        setupGrid(favoritesList, true);
        adapter.setSupportsDoubleClick(false);
        cancelFab = (FloatingActionButton) rootView.findViewById(R.id.fab_cancel);
        cancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setSelectedAll(false);
                hideFab(cancelFab, selectAllFab);
            }
        });
        deleteFab = (FloatingActionButton) rootView.findViewById(R.id.fab_delete);
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!adapter.isAnySelected()) {
                    Toast.makeText(getContext(), R.string.toast_delete_favorite,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    List<Image> selected = adapter.getSelectedImages();
                    int failedUnlikes = 0;
                    List<Image> unliked = new ArrayList<>();
                    for (Image image : selected) {
                        if (!image.unlike()) {
                            ++failedUnlikes;
                        } else {
                            unliked.add(image);
                        }
                    }
                    if (failedUnlikes > 0) {
                        Toast.makeText(getContext(), R.string.toast_delete_favorite_failed + failedUnlikes,
                                Toast.LENGTH_SHORT).show();
                    }
                    adapter.setSelectedAll(false);
                    hideFab(cancelFab, selectAllFab);

                    List<Object> newData = new ArrayList<>();
                    for (Object dataItem : data) {
                        boolean found = false;
                        for (Image unlikedImage : unliked) {
                            if (dataItem instanceof Image) {
                                if (unlikedImage.isMe((Image)dataItem)) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            newData.add(dataItem);
                        }
                    }

                    data = newData;
                    adapter.setData(data);
                }

            }
        });
        selectAllFab = (FloatingActionButton) rootView.findViewById(R.id.fab_select_all);
        selectAllFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setSelectedAll(true);
            }
        });
        return rootView;
    }

    private List<Object> getFavorites() {
        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        try {
            String getResult = getRequest.execute(getFavoritesRequest()).get();
            if (null == getResult) {
                throw new InterruptedException();
            }
            return JsonHelper.getFavoritesImageList(getResult);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private String[] getFavoritesRequest() {
        String[] result = new String[3];
        result[0] = "http://kulon.jwma.ru/api/v1/favorites";
        result[1] = "Authorization";
        StringBuilder token = new StringBuilder("Bearer ");
        token.append(MyPoshApplication.getCurrentToken().getToken());
        result[2] = new String(token);

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private  boolean isSelectionInProcess() {
        return cancelFab.getVisibility() == View.VISIBLE &&
                selectAllFab.getVisibility() == View.VISIBLE;
    }

    @Override
    protected void setupGrid(List<Object> images, boolean initialSetup) {
        super.setupGrid(images, initialSetup);
        adapter.setClickListener(new ImageAdapter.ClickListener() {
            @Override
            public void onSingleClick(View view, int position) {
                if(isSelectionInProcess()) {
                    adapter.setSelected(position, !adapter.isSelected(position));
                }
            }

            @Override
            public void onDoubleClick(View view, int position) {
                //not supported
            }

            @Override
            public boolean onLongClick(View view, int position) {
                if (!isSelectionInProcess()) {
                    showFab(selectAllFab, cancelFab);
                }
                adapter.setSelected(position, !adapter.isSelected(position));
                return true;
            }
        });
    }

    private void showFab(FloatingActionButton... fab) {

        List<Animator> list = new ArrayList<>();
        for (int i = 0; i < fab.length; i++) {
            fab[i].setVisibility(View.VISIBLE);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab[i], "scaleX", 0f, 1.2f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab[i], "scaleY", 0f, 1.2f, 1f);
            list.add(scaleX);
            list.add(scaleY);
        }

        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.playTogether(list);
        set.start();

    }


    private void hideFab(final FloatingActionButton... fab) {

        List<Animator> list = new ArrayList<>();
        for (int i = 0; i < fab.length; i++) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab[i], "scaleX", 1f, 1.2f, 0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab[i], "scaleY", 1f, 1.2f, 0f);
            list.add(scaleX);
            list.add(scaleY);
        }

        AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                for (int i = 0; i < fab.length; i++) {
                    fab[i].setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.setDuration(300);
        set.playTogether(list);
        set.start();

    }

}
