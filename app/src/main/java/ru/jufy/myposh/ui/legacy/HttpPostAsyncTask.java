package ru.jufy.myposh.ui.legacy;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BorisDev on 02.08.2017.
 */

public class HttpPostAsyncTask extends AsyncTask<String, Void, String> {
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private static final String crlf = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary =  "****";
    private HashMap<String, String> reqProps = null;
    private Bitmap image = null;

    @Override
    protected String doInBackground(String... params) {
        String stringUrl = params[0];
        String body = (params.length > 1) ? params[1] : "";
        byte[] byteData;
        String result;
        String inputLine;
        try {
            URL myUrl = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(getRequestMethod());
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (null != reqProps) {
                for (Map.Entry<String, String> prop : reqProps.entrySet()) {
                    connection.setRequestProperty(prop.getKey(), prop.getValue());
                }
            }

            OutputStream os = connection.getOutputStream();
            byteData = body.getBytes("UTF-8");
            if (null != image) {
                os.write(twoHyphens.getBytes("UTF-8"));
                os.write(boundary.getBytes("UTF-8"));
                os.write(crlf.getBytes("UTF-8"));
                os.write(byteData);
                image.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.write(crlf.getBytes("UTF-8"));
                os.write(twoHyphens.getBytes("UTF-8"));
                os.write(boundary.getBytes("UTF-8"));
                os.write(twoHyphens.getBytes("UTF-8"));
                os.write(crlf.getBytes("UTF-8"));
            } else {
                os.write(byteData);
            }
            os.flush();
            os.close();

            InputStreamReader streamReader;

            if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                streamReader = new
                        InputStreamReader(connection.getInputStream());
            } else {
                streamReader = new
                        InputStreamReader(connection.getErrorStream());
            }

            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    String getRequestMethod() {
        return "POST";
    }

    public void setRequestProperties(HashMap<String, String> props) {
        reqProps = props;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getBoundary() {
        return boundary;
    }

    public String getCrLf() {
        return crlf;
    }
}
