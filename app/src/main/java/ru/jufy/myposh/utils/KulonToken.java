package ru.jufy.myposh.utils;

import java.util.Date;

/**
 * Created by BorisDev on 31.07.2017.
 */

public class KulonToken {

    public KulonToken() {
        this.token = "";
        this.expirationDate = null;
    }

    public KulonToken(String token, Date expDate) {
        this.token = token;
        this.expirationDate = expDate;
    }

    public final String getToken() {
        return token;
    }

    public final Date getExpirationDate() {
        return expirationDate;
    }

    private final String token;
    private final Date expirationDate;
}
