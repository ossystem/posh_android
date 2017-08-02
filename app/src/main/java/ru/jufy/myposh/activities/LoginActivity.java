package ru.jufy.myposh.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.HttpGetAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;

public class LoginActivity extends AppCompatActivity {

    private boolean isResumed = false;

    static String vkRequest = "http://kulon.jwma.ru/api/v1/socialite?provider=vkontakte";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.vk_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVkRequest();
            }
        });
    }

    private void sendVkRequest() {
        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
        //Perform the doInBackground method, passing in our url
        try {
            String getResult = getRequest.execute(vkRequest).get();
            String authLink = JsonHelper.getSocialAuthLink(getResult);
            Intent i = new Intent(this, WebViewActivity.class);
            Bundle b = new Bundle();
            b.putString("link", authLink);
            i.putExtras(b);
            startActivityForResult(i, 1);
            //Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            Toast.makeText(this, "Request for VK auth has been interrupted", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (ExecutionException e) {
            Toast.makeText(this, "Request for VK auth has failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (Activity.RESULT_OK == resultCode) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "Failed to authorize!", Toast.LENGTH_LONG).show();
        }
    }
}
