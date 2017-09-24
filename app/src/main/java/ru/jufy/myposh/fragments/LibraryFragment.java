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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_library, container, false);
        shadowBg = rootView.findViewById(R.id.shadow_bg);

        fabText = (FloatingActionButton)
                rootView.findViewById(R.id.fab_text);
        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabTextClick();
            }
        });

        List<Object> poshiksList = getPurchasedPoshiks();
        poshiksList.addAll(getHandmadePoshiks());
        setupGrid(poshiksList, true);

        return rootView;
    }

    private List<Object> getHandmadePoshiks() {
        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        try {
            String getResult = getRequest.execute(getHandmadeRequest()).get();
            if (null == getResult) {
                throw new InterruptedException();
            }
            List<Object> result = JsonHelper.getHandmadeImageList(getResult);
            if (result.size() > 0) {
                result.add(0, getString(R.string.title_my_poshiks));
            }
            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
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

    private List<Object> getPurchasedPoshiks() {
        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        try {
            String getResult = getRequest.execute(getPurchasedRequest()).get();
            if (null == getResult) {
                throw new InterruptedException();
            }
            List<Object> result = JsonHelper.getPurchasedImageList(getResult);
            result.add(0, getString(R.string.title_purchases));
            return result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private String[] getPurchasedRequest() {
        return getRequestAuthorized("http://kulon.jwma.ru/api/v1/poshiks/purchase");
    }

    private String[] getHandmadeRequest() {
        return getRequestAuthorized("http://kulon.jwma.ru/api/v1/poshiks/my");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IntentDispatcherActivity)) {
            throw new IllegalArgumentException();
        }
    }
}
