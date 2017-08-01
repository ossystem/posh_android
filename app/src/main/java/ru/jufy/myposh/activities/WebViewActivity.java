package ru.jufy.myposh.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.utils.HttpGetAsyncTask;
import ru.jufy.myposh.utils.JsonParser;
import ru.jufy.myposh.utils.KulonToken;

import static ru.jufy.myposh.activities.LoginActivity.vkRequest;

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
                    HttpGetAsyncTask getRequest = new HttpGetAsyncTask();
                    try {
                        String getResult = getRequest.execute(url).get();
                        MyPoshApplication.currentToken = JsonParser.getToken(getResult);
                        Intent intent = new Intent();
                        intent.putExtra("token", MyPoshApplication.currentToken.token);
                        intent.putExtra("date", MyPoshApplication.currentToken.date);
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
                else
                {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
    }
}
