package ru.jufy.myposh.model.data.server.interceptors;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import ru.jufy.myposh.model.storage.UserPreferences;

/**
 * Created by rolea on 6/10/2017.
 */

public class AuthenticationInterceptor implements Interceptor {

    private String authToken;
    private UserPreferences preferences;

    public AuthenticationInterceptor(UserPreferences preferences) {
        this.preferences = preferences;
        this.authToken = preferences.getToken();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        if (preferences.isLoggedIn()) {
            authToken = preferences.getToken();
            builder.header("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}
