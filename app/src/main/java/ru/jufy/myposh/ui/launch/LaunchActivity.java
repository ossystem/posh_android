package ru.jufy.myposh.ui.launch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.jufy.mgtshr.ui.base.BaseActivity;

import org.json.JSONException;

import javax.inject.Inject;

import io.branch.referral.Branch;
import ru.jufy.myposh.R;
import ru.jufy.myposh.Screens;
import ru.jufy.myposh.entity.SocialTypes;
import ru.jufy.myposh.presentation.launch.LaunchMvpView;
import ru.jufy.myposh.presentation.launch.LaunchPresenter;
import ru.jufy.myposh.ui.auth.AuthPhoneFragment;
import ru.jufy.myposh.ui.auth.AuthSocialActivity;
import ru.jufy.myposh.ui.global.WebViewActivity;
import ru.jufy.myposh.ui.main.MainActivity;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.android.SupportAppNavigator;

public class LaunchActivity extends BaseActivity implements LaunchMvpView {

    @Inject
    LaunchPresenter<LaunchMvpView> presenter;

    @Inject
    NavigatorHolder navigatorHolder;


    private Navigator navigator = new SupportAppNavigator(this, R.id.fragment_frame) {

        @Override
        protected Fragment createFragment(String screenKey, Object data) {
            switch (screenKey) {
                case Screens.LOGIN_ACTIVITY_SCREEN:
                    return AuthPhoneFragment.Companion.newInstance();
            }

            return null;
        }

        @Override
        protected Intent createActivityIntent(Context context, String screenKey, Object data) {
            switch (screenKey) {
                case Screens.MAIN_ACTIVITY_SCREEN:
                    return new Intent(LaunchActivity.this, MainActivity.class);
                case Screens.AUTHENTICATE_SOCIAL: {
                    Intent i = new Intent(context, AuthSocialActivity.class);
                    Bundle b = new Bundle();
                    b.putString(AuthSocialActivity.Companion.getSOCIAL_TYPE(), ((SocialTypes)data).getValue());
                    b.putInt(WebViewActivity.Companion.getACTION(), WebViewActivity.Companion.getACTION_AUTHORIZE());
                    i.putExtras(b);
                    return i;
                }
            }
            return null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();


        // Branch init
        Branch.getInstance().initSession((referringParams, error) -> {
            if (error == null) {
                Log.i("BRANCH SDK", referringParams.toString());
                try {
                    boolean isFirst = referringParams.getBoolean("+is_first_session");
                    String referralCode = referringParams.getString("referral_code");

                    if (isFirst && referralCode!= null && !referralCode.isEmpty()){
                        presenter.saveReferrall(referralCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("BRANCH SDK", error.getMessage());
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        presenter.onAttach(this);
        setUp();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        navigatorHolder.removeNavigator();
        super.onPause();
    }

    @Override
    protected void setUp() {

    }
}
