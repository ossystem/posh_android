package ru.jufy.myposh.ui.legacy;

/**
 * Created by BorisDev on 02.08.2017.
 */

public class HttpDelAsyncTask extends HttpPostAsyncTask {

    @Override
    String getRequestMethod() {
        return "DELETE";
    }
}
