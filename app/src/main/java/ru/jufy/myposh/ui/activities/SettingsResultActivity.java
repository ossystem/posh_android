package ru.jufy.myposh.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;

public class SettingsResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_result);
        TextView debugOutput = findViewById(R.id.debugOutput);
        if (null == MyPoshApplication.Companion.getCurrentToken()) {
            debugOutput.setText("No token available");
        } else {
            debugOutput.setText("Token:\n" + MyPoshApplication.Companion.getCurrentToken().getToken());
        }
    }
}
