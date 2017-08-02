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
        byte[] byteData = null;
        String result;
        String inputLine;
        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);
            //Create a connection
            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //OutputStream os = connection.getOutputStream();
            //byteData = parameters.getBytes("UTF-8");
            //os.write(byteData);

            //Connect to our url
            connection.connect();
            int responseCode= connection.getResponseCode();
            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }
}
