package ru.jufy.myposh;

import android.app.Application;
import android.content.Context;

import android.widget.Toast;

import java.util.Date;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.utils.HttpPostAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;
import ru.jufy.myposh.utils.KulonToken;

/**
 * Created by BorisDev on 26.07.2017.
 */

public class MyPoshApplication extends Application {

    private static final String DEBUG_URL = "https://posh.jwma.ru/api/v1/";
    public static final String DOMAIN = DEBUG_URL;

    private static KulonToken currentToken = null;
    private static Timer expTimer = null;

    private static MyPoshApplication app;

    private static long MS_TO_START_REFRESH_BEFORE_TOKEN_EXP = 10000;

    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static Context getContext() {
        return  app.getApplicationContext();
    }

    public static KulonToken getCurrentToken() {
        return currentToken;
    }

    public static void onNewTokenObtained(KulonToken newToken) {
        currentToken = newToken;
    }

}
