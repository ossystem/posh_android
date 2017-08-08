package ru.jufy.myposh.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.data;

/**
 * Created by BorisDev on 02.08.2017.
 */

public class HttpDelAsyncTask extends HttpPostAsyncTask {

    @Override
    String getRequestMethod() {
        return "DELETE";
    }
}
