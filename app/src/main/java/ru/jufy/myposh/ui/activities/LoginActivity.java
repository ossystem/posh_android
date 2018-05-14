package ru.jufy.myposh.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jufy.mgtshr.ui.base.BaseActivity;

import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.ui.fragments.LoginFragment;
import ru.jufy.myposh.ui.utils.HttpGetAsyncTask;
import ru.jufy.myposh.ui.utils.JsonHelper;

public class LoginActivity extends BaseActivity {

    private static String instagramRequest = MyPoshApplication.Companion.getDOMAIN() + "social/instagram";
    private static String fbRequest = MyPoshApplication.Companion.getDOMAIN() + "social/facebook";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUp();
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment_frame, fragment);
        transaction.commit();
    }

    public void authorizeInstagram() {
        sendSocialAuthRequest(instagramRequest);
    }

    public void authorizeFB() {
        sendSocialAuthRequest(fbRequest);
    }

    public void showEmailLogin() {
        showFragment(LoginFragment.newInstance());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (Activity.RESULT_OK == resultCode) {
            startMainActivity();
        } else {
            Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_LONG).show();
        }
    }

    public void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void sendSocialAuthRequest(String requestLink) {
        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        try {
            String getResult = getRequest.execute(requestLink).get();
            if (null == getResult) {
                throw new InterruptedException();
            }
            String authLink = JsonHelper.getSocialAuthLink(getResult);
            Intent i = new Intent(this, WebViewActivity.class);
            Bundle b = new Bundle();
            b.putString(WebViewActivity.URL, authLink);
            b.putInt(WebViewActivity.ACTION, WebViewActivity.ACTION_AUTHORIZE);
            i.putExtras(b);
            startActivityForResult(i, 1);
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(this, R.string.social_auth_failed, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void setUp() {
        showEmailLogin();
    }
}
