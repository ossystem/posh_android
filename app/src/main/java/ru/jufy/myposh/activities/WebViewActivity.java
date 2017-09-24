package ru.jufy.myposh.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.HttpGetAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;

public class WebViewActivity extends AppCompatActivity {

    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Bundle coming = getIntent().getExtras();
        link = coming.getString("link");
    }

    @Override
    protected void onStart() {
        super.onStart();

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl(link);

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().contains("kulon.jwma.ru")) {
                    Intent intent = new Intent();
                    HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
                    try {
                        String getResult = getRequest.execute(url).get();
                        if (null == getResult) {
                            throw new InterruptedException();
                        }
                        MyPoshApplication.onNewTokenObtained(JsonHelper.getToken(getResult));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } catch (InterruptedException | ExecutionException e) {
                        setResult(Activity.RESULT_CANCELED, intent);
                        e.printStackTrace();
                    }
                    return false;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
    }
}
