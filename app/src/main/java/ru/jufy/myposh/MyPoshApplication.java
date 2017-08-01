package ru.jufy.myposh;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import ru.jufy.myposh.activities.LoginActivity;
import ru.jufy.myposh.utils.KulonToken;

/**
 * Created by BorisDev on 26.07.2017.
 */

public class MyPoshApplication extends Application {
    public static KulonToken currentToken = new KulonToken();
}
