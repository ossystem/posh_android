package ru.jufy.myposh.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.jufy.myposh.R;
import ru.jufy.myposh.activities.IntentDispatcherActivity;
import ru.jufy.myposh.adapters.ImageAdapter;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.utils.AnimatorUtils;
import ru.jufy.myposh.views.ArcBarView;


public class LibraryFragment extends ImageGridFragment {

    ArcLayout arcLayout;
    FloatingActionButton fabAdd;
    FloatingActionButton fabTakePhoto;
    View shadowBg;


    public LibraryFragment() {
        // Required empty public constructor
    }

    public static LibraryFragment newInstance() {
        LibraryFragment fragment = new LibraryFragment();
        return fragment;
    }


    public void onPhotoActivityResult() {
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_library, container, false);

        fabAdd = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick(view);
            }
        });
        fabTakePhoto = (FloatingActionButton) rootView.findViewById(R.id.fab_take_photo);
        fabTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((IntentDispatcherActivity)getContext()).dispatchTakePictureIntent();
            }
        });
        shadowBg = rootView.findViewById(R.id.shadow_bg);
        arcLayout =(ArcLayout) rootView.findViewById(R.id.add_menu);

        setupGrid(new ArrayList<Image>(Collections.nCopies(7, new Image())), true);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IntentDispatcherActivity)) {
            throw new IllegalArgumentException();
        }
    }


    //--------------- menu animation methods --------------------

    private void onFabClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

    @SuppressWarnings("NewApi")
    private void showMenu() {

        //Buttons
        arcLayout.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);

        //Background
        AlphaAnimation animBg = new AlphaAnimation(0f, 1.0f);
        shadowBg.setVisibility(View.VISIBLE);
        animBg.setDuration(400);
        shadowBg.setAlpha(1f);

        //start
        shadowBg.startAnimation(animBg);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {

        //Buttons
        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                arcLayout.setVisibility(View.INVISIBLE);
            }
        });

        //Background
        AlphaAnimation animBg = new AlphaAnimation(1.0f, 0f);
        animBg.setDuration(400);
        shadowBg.setAlpha(1f);
        animBg.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                shadowBg.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //start
        shadowBg.startAnimation(animBg);
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {

        float dx = fabAdd.getX() - item.getX();
        float dy = fabAdd.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);
        item.setScaleX(0f);
        item.setScaleY(0f);
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f),
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = fabAdd.getX() - item.getX();
        float dy = fabAdd.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy),
                AnimatorUtils.scaleX(1f, 0f),
                AnimatorUtils.scaleY(1f, 0f)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    //---------------------------- end menu animation methods ----------------
}
