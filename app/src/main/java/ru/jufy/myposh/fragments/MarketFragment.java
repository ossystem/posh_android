package ru.jufy.myposh.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.adapters.ImageAdapter;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.utils.AnimatorUtils;
import ru.jufy.myposh.utils.HttpGetAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;

import static android.R.attr.fragment;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class MarketFragment extends ImageGridFragment {

    FloatingActionButton fabSearch;
    FloatingActionButton fabCategory;
    View shadowBg;
    ArcLayout arcLayout;

    public MarketFragment() {
    }

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        return fragment;
    }

    @Override
    protected void setupGrid(List<Image> images) {
        super.setupGrid(images);
        adapter.setClickListener(new ImageAdapter.ClickListener() {
            @Override
            public void onSingleClick(View view, int position) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ImageFragment imgFrag = new ImageFragment();
                imgFrag.setImage(adapter.getImage(position));
                transaction.replace(R.id.fragment_frame, imgFrag);
                transaction.commit();
            }

            @Override
            public void onDoubleClick(View view, int position) {
                //not supported
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_market, container, false);
        fabSearch = (FloatingActionButton)
                rootView.findViewById(R.id.fab_search);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabSearchClick(view);
            }
        });

        fabCategory = (FloatingActionButton) rootView.findViewById(R.id.fab_category);
        fabCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabCategoryClick(view);
            }
        });
        arcLayout = (ArcLayout) rootView.findViewById(R.id.search_menu);
        shadowBg = rootView.findViewById(R.id.shadow_bg);
        List<Image> poshiksList = getPoshiks();
        setupGrid(poshiksList);

        return rootView;
    }

    private void onFabCategoryClick(View view) {
        FrameLayout layout = (FrameLayout) rootView.findViewById(R.id.tag_and_category);
        layout.removeAllViews();
        RecyclerView categoriesView = new RecyclerView(getContext());
        categoriesView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewGroup.MarginLayoutParams marginLayoutParams =
                new ViewGroup.MarginLayoutParams(categoriesView.getLayoutParams());
        marginLayoutParams.setMargins(50, 0, 50, 0);
        categoriesView.setLayoutParams(marginLayoutParams);
        categoriesView.setBackgroundColor(0xDFFFFFFF);
        String[] categories = getCategories();
        categoriesView.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoriesAdapter adapter = new CategoriesAdapter(getContext(), categories);
        categoriesView.setAdapter(adapter);
        layout.addView(categoriesView);
    }

    private String[] getCategories() {
        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        try {
            String getResult = getRequest.execute(getCategoriesRequest()).get();
            if (null == getResult) {
                throw new InterruptedException();
            }
            return JsonHelper.getCategories(getResult);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private String[] getCategoriesRequest() {
        String[] result = new String[3];
        result[0] = "http://kulon.jwma.ru/api/v1/categories";
        result[1] = "Authorization";
        StringBuilder token = new StringBuilder("Bearer ");
        token.append(MyPoshApplication.getCurrentToken().getToken());
        result[2] = new String(token);

        return result;
    }

    class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryHolder> {

        String[] items;
        Context context;

        CategoriesAdapter(Context context, String[] items) {
            super();
            this.context = context;
            this.items = items;
        }

        @Override
        public CategoriesAdapter.CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item, parent, false);
            return new CategoryHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoriesAdapter.CategoryHolder holder, int position) {
            holder.item.setText(items[position]);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        public class CategoryHolder extends RecyclerView.ViewHolder {
            TextView item;
            public CategoryHolder(View itemView) {
                super(itemView);
                item = (TextView)itemView.findViewById(R.id.category_item_text);;
            }
        }
    }

    private List<Image> getPoshiks() {
        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        try {
            String getResult = getRequest.execute(getMarketRequest()).get();
            if (null == getResult) {
                throw new InterruptedException();
            }
            return JsonHelper.getMarketImageList(getResult);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private String[] getMarketRequest() {
        String[] result = new String[3];
        result[0] = "http://kulon.jwma.ru/api/v1/market";
        result[1] = "Authorization";
        StringBuilder token = new StringBuilder("Bearer ");
        token.append(MyPoshApplication.getCurrentToken().getToken());
        result[2] = new String(token);

        return result;
    }

//--------------- menu animation methods --------------------

    private void onFabSearchClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

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

        setShadowOn();

        animSet.start();
    }

    private void setShadowOn() {
        setShadow(0f, 1.0f, View.VISIBLE);
    }

    private void setShadow(float fromAlpha, float toAlpha, int visible) {
        AlphaAnimation animBg = new AlphaAnimation(fromAlpha, toAlpha);
        shadowBg.setVisibility(visible);
        animBg.setDuration(400);
        shadowBg.setAlpha(1f);
        shadowBg.startAnimation(animBg);
    }

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

        setShadowOff();

        animSet.start();

        FrameLayout layout = (FrameLayout) rootView.findViewById(R.id.tag_and_category);
        layout.removeAllViews();
    }

    private void setShadowOff() {
        setShadow(1.0f, 0f, View.GONE);
    }

    private Animator createShowItemAnimator(View item) {

        float dx = fabSearch.getX() - item.getX();
        float dy = fabSearch.getY() - item.getY();

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
        float dx = fabSearch.getX() - item.getX();
        float dy = fabSearch.getY() - item.getY();

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
}
