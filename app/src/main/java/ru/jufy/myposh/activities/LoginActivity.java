package ru.jufy.myposh.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.fragments.EmailLoginFragment;
import ru.jufy.myposh.fragments.LoginTypesFragment;
import ru.jufy.myposh.utils.HttpGetAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;

public class LoginActivity extends AppCompatActivity {

    private static String vkRequest = MyPoshApplication.DOMAIN + "social/vkontakte";
    private static String fbRequest = MyPoshApplication.DOMAIN + "social/facebook";

    private FragmentTransaction transaction;
    private LoginTypesFragment loginTypesFragment;
    private EmailLoginFragment emailLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTypesFragment = new LoginTypesFragment();
        emailLoginFragment = new EmailLoginFragment();

        showLoginTypes();
    }

    private void showFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_frame, fragment);
        transaction.commit();
    }

    public void authorizeVK() {
        sendSocialAuthRequest(vkRequest);
    }

    public void authorizeFB() {
        sendSocialAuthRequest(fbRequest);
    }

    public void showLoginTypes() {
        showFragment(loginTypesFragment);
    }
    public void showEmailLogin() {
        showFragment(emailLoginFragment);
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
}
