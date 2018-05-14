package ru.jufy.myposh.ui.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by BorisDev on 28.07.2017.
 */

public class HttpGetAsyncTask extends AsyncTask<String, Void, String> {

    static final String REQUEST_METHOD = "GET";
    static final int READ_TIMEOUT = 15000;
    static final int CONNECTION_TIMEOUT = 15000;

    private Map<String,List<String>> responseHeaders = null;
    private String contentType;
    private File file;

    @Override
    protected String doInBackground(String... params) {
        String stringUrl = params[0];
        int propertiesCount = params.length - 1;
        if ((propertiesCount % 2) != 0) {
            --propertiesCount;
        }
        String result = "";
        String inputLine;
        try {
            URL myUrl = new URL(stringUrl);

            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            if (propertiesCount > 0) {
                for(int i = 0; i < propertiesCount; i += 2) {
                    connection.setRequestProperty(params[i + 1], params[i + 2]);
                }
            }

            connection.connect();

            responseHeaders = connection.getHeaderFields();
            if (null != responseHeaders
                    && responseHeaders.get("Content-Type") != null) {
                contentType = responseHeaders.get("Content-Type").get(0);
            }

            if (receivedDataIsBinary()) {
                int size = 0;
                if (responseHeaders.get("Content-Length") != null) {
                    for (String length : responseHeaders.get("Content-Length")) {
                        size = Integer.parseInt(length);
                    }
                }
                if (size > 0) {
                    InputStream in = connection.getInputStream();
                    OutputStream out = new FileOutputStream(file);
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = in.read(buf)) > 0) {
                        out.write(buf, 0, n);
                    }
                    out.flush();
                    out.close();
                }

            } else {

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
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    public boolean receivedDataIsBinary() {
        if (null != contentType && (contentType.equals("image/jpeg") || contentType.equals("application/octet-stream"))) {
            return true;
        }
        return false;
    }

    public void setFileToStoreImage(File tempFile) {
        file = tempFile;
    }
}
