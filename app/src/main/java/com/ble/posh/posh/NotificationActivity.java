package com.ble.posh.posh;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ru.jufy.myposh.activities.MainActivity;

/**
 * Created by Admin on 24.07.2017.
 */

public class NotificationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BOOT","NotificationActivity onCreate");
        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
/*
            final Intent parentIntent = new Intent(this, FeaturesActivity.class);
            parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
*/
            final Intent startAppIntent = new Intent(this, MainActivity.class);
            startAppIntent.putExtras(getIntent().getExtras());
            startActivities(new Intent[] { startAppIntent });
            Log.d("BOOT","NotificationActivity isTaskRoot");
        }

        // Now finish, which will drop the user in to the activity that was at the top
        //  of the task stack
        finish();
    }
}
