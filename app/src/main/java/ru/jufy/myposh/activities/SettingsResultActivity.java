package ru.jufy.myposh.activities;

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
        TextView debugOutput = (TextView) findViewById(R.id.debugOutput);
        if (null == MyPoshApplication.getCurrentToken()) {
            debugOutput.setText("No token available");
        } else {
            String expDate = (null == MyPoshApplication.getCurrentToken().getExpirationDate()) ? "No expiration date"
                    : MyPoshApplication.getCurrentToken().getExpirationDate().toString();
            debugOutput.setText("Token:\n" + MyPoshApplication.getCurrentToken().getToken()
                    + "\nExpiration date:\n" + expDate);
        }
    }
}
