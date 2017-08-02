package ru.jufy.myposh.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.data;

/**
 * Created by BorisDev on 02.08.2017.
 */

public class HttpPostAsyncTask extends AsyncTask<String, Void, String> {
    static final String REQUEST_METHOD = "POST";
    static final int READ_TIMEOUT = 15000;
    static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... params) {
        String stringUrl = params[0];
        String parameters = params[1];
        byte[] byteData;
        String result;
        String inputLine;
        try {
            URL myUrl = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = connection.getOutputStream();
            byteData = parameters.getBytes("UTF-8");
            os.write(byteData);
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            } else {
                result = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }
}
