package ru.jufy.myposh.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.ui.utils.HttpGetAsyncTask;
import ru.jufy.myposh.ui.utils.JsonHelper;

import static java.util.ResourceBundle.getBundle;

public class WebViewActivity extends AppCompatActivity {

    public static final String URL = "ru.jufy.myposh.ui.activities.WebViewActivity.URL";
    public static final String ACTION = "ru.jufy.myposh.ui.activities.WebViewActivity.ACTION";
    public static final String HEADERS = "ru.jufy.myposh.ui.activities.WebViewActivity.HEADERS";

    public static final int ACTION_AUTHORIZE = 0;
    public static final int ACTION_SHOW_WITH_HEADERS = 1;

    private String link;
    private int action = -1;
    private HashMap<String, String> httpHeaders = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        link = getIntent().getStringExtra(URL);
        action = getIntent().getIntExtra(ACTION, -1);
        if (ACTION_SHOW_WITH_HEADERS == action) {
            httpHeaders = (HashMap<String, String>)getIntent().getSerializableExtra(HEADERS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        WebView myWebView = (WebView) findViewById(R.id.webview);

        if (ACTION_AUTHORIZE == action) {
            myWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (Uri.parse(url).getHost().contains("posh.jwma.ru")) {
                        Intent intent = new Intent();
                        HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
                        try {
                            String getResult = getRequest.execute(url).get();
                            if (null == getResult) {
                                throw new InterruptedException();
                            }
                            MyPoshApplication.Companion.onNewTokenObtained(JsonHelper.getToken(getResult));
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

        if (ACTION_SHOW_WITH_HEADERS == action) {
            myWebView.loadUrl(link, httpHeaders);
        } else {
            myWebView.loadUrl(link);
        }
    }
}
