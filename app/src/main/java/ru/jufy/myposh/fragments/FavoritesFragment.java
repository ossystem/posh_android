package ru.jufy.myposh.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.jufy.myposh.adapters.ImageAdapter;
import ru.jufy.myposh.adapters.ImageGridDecoration;
import ru.jufy.myposh.R;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.data.ImageRepository;
import ru.jufy.myposh.views.ArcLayout;


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
        setupGrid(null);
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
                    List<Image> selected = adapter.getSelected();
                    adapter.setSelectedAll(false);
                    hideFab(cancelFab, selectAllFab);
                    data = new ArrayList<>(Collections.nCopies(data.size() - selected.size(),
                            new Image()));
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

    @Override
    public void onResume() {
        super.onResume();

    }

    private  boolean isSelectionInProcess() {
        return cancelFab.getVisibility() == View.VISIBLE &&
                selectAllFab.getVisibility() == View.VISIBLE;
    }

    @Override
    protected void setupGrid(ImageRepository imageRepository) {
        super.setupGrid(imageRepository);
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
