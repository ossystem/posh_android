package ru.jufy.myposh.ui.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import ru.jufy.myposh.MyPoshApplication;

/**
 * Created by BorisDev on 04.08.2017.
 */

public class AuthInterceptor implements Interceptor {

    @Override
    public synchronized Response intercept(Chain chain) throws IOException {
        final Request original = chain.request();
        final Request.Builder requestBuilder = original.newBuilder()
                .header("Authorization", "Bearer " + MyPoshApplication.getCurrentToken().getToken())
                .method(original.method(), original.body());
        return chain.proceed(requestBuilder.build());
    }
}
