package ru.jufy.myposh.model.data.server.response;

/**
 * Created by rolea on 18.09.2017.
 */

public class ApiError<T> {
    private T error;

    public T getError() {
        return error;
    }

    public void setError(T error) {
        this.error = error;
    }
}
