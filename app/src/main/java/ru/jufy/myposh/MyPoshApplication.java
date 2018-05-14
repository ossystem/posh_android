package ru.jufy.myposh;

import android.app.Application;
import android.content.Context;

import ru.jufy.myposh.models.data.KulonToken;

/**
 * Created by BorisDev on 26.07.2017.
 */

public class MyPoshApplication extends Application {

    private static final String DEBUG_URL = "https://posh.jwma.ru/api/v1/";
    public static final String DOMAIN = DEBUG_URL;

    private static KulonToken currentToken = null;

    private static MyPoshApplication app;


    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static Context getContext() {
        return app.getApplicationContext();
    }

    public static KulonToken getCurrentToken() {
        return currentToken;
    }

    public static void onNewTokenObtained(KulonToken newToken) {
        currentToken = newToken;
    }

}
