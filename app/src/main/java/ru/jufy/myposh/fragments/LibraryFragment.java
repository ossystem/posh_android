package ru.jufy.myposh.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.R;
import ru.jufy.myposh.activities.IntentDispatcherActivity;
import ru.jufy.myposh.adapters.ImageAdapter;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.utils.AnimatorUtils;
import ru.jufy.myposh.utils.HttpGetAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;
import ru.jufy.myposh.views.ArcBarView;

import static ru.jufy.myposh.utils.JsonHelper.getPurchasedImageList;


public class LibraryFragment extends ImageGridFragment {
    private static final int SHOW_PURCHASED = 0;
    private static final int SHOW_HANDMADE = 1;

    int currentListType = SHOW_PURCHASED;

    View shadowBg;
    FloatingActionButton fabText;
    private final TextEditorFragment textFrag;

    public LibraryFragment() {
        textFrag = new TextEditorFragment();
    }

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_library, container, false);
        shadowBg = rootView.findViewById(R.id.shadow_bg);

        rootView.findViewById(R.id.buttonPurchased).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SHOW_PURCHASED != currentListType) {
                    showPurchased();
                }
            }
        });

        rootView.findViewById(R.id.buttonHandmade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SHOW_HANDMADE != currentListType) {
                    showHandmade();
                }
            }
        });

        fabText = (FloatingActionButton)
                rootView.findViewById(R.id.fab_text);
        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabTextClick();
            }
        });

        currentListType = SHOW_PURCHASED;
        resetLastDisplayedPage();
        List<Object> poshiksList = getAllPoshiksAtPage(1);
        setupGrid(poshiksList, true);

        return rootView;
    }

    private void showPurchased() {
        currentListType = SHOW_PURCHASED;
        resetLastDisplayedPage();
        List<Object> poshiksList = getAllPoshiksAtPage(1);
        adapter.setData(poshiksList);
    }

    private void showHandmade() {
        currentListType = SHOW_HANDMADE;
        resetLastDisplayedPage();
        List<Object> poshiksList = getAllPoshiksAtPage(1);
        adapter.setData(poshiksList);
    }

    @Override
    protected List<Object> getAllPoshiksAtPage(int page) {
        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        try {
            String getResult = getRequest.execute(getAllPoshiksAtPageRequest(page)).get();
            if (null == getResult) {
                throw new InterruptedException();
            }
            setTotalPagesNum(JsonHelper.getTotalNumPages(getResult));
            return getPoshiksList(getResult);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    String[] getAllPoshiksAtPageRequest(int page) {
        switch(currentListType) {
            case SHOW_PURCHASED:
                return getPurchasedRequest(page);
            case SHOW_HANDMADE:
                return getHandmadeRequest(page);
        }
        return null;
    }

    private List<Object> getPoshiksList(String jsonString) {
        switch(currentListType) {
            case SHOW_PURCHASED:
                return JsonHelper.getPurchasedImageList(jsonString);
            case SHOW_HANDMADE:
                return JsonHelper.getHandmadeImageList(jsonString);
        }
        return null;
    }

    private void onFabTextClick() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_frame, textFrag);
        transaction.commit();
    }

    @Override
    protected void setupGrid(List<Object> images, boolean initialSetup) {
        super.setupGrid(images, initialSetup);
        adapter.setClickListener(new ImageClickListener());
    }

    private String[] getPurchasedRequest(int page) {
        return getRequestAuthorized("http://kulon.jwma.ru/api/v1/poshiks/purchase?page=" + page);
    }

    private String[] getHandmadeRequest(int page) {
        return getRequestAuthorized("http://kulon.jwma.ru/api/v1/poshiks/my?page=" + page);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IntentDispatcherActivity)) {
            throw new IllegalArgumentException();
        }
    }
}
