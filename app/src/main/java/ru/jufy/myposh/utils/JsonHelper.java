package ru.jufy.myposh.utils;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.data.Category;
import ru.jufy.myposh.data.HandmadeImage;
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.data.MarketImage;

/**
 * Created by BorisDev on 31.07.2017.
 */

public class JsonHelper {

    private static int SOCIAL_AUTH_RSP_DATA_IDX = 0;

    @NonNull
    public static String getSocialAuthLink(String jsonString) {
        try {
            JSONObject link = getJsonObjectFromData(jsonString);
            return link.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static JSONObject getJsonObjectFromData(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        return json.getJSONObject("data");
    }

    @NonNull
    public static KulonToken getToken(String jsonString) {
        try {
            JSONObject tokenData = getJsonObjectFromData(jsonString);
            return new KulonToken(tokenData.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
            return new KulonToken();
        }
    }

    private static Date stringToDate(String aDate, String aFormat) {
        if(aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        simpledateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpledateformat.parse(aDate, pos);
    }

    @NonNull
    public static String convertTokenToJson() {
        JSONObject data = new JSONObject();
        try {
            data.put("token", MyPoshApplication.getCurrentToken().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    @NonNull
    public static List<Object> getMarketImageList(String jsonString) {
        try {
            JSONObject poshiks = getJsonObjectFromData(jsonString);
            String poshiksArray = poshiks.getString("marketPoshiks");
            JSONArray jsonarray = new JSONArray(poshiksArray);
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Image item = new MarketImage(jsonobject.getInt("id"), jsonobject.getString("extension"),
                        jsonobject.getBoolean("is_favorite"), jsonobject.getBoolean("is_purchased"));
                result.add(item);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @NonNull
    public static List<Object> getFavoritesImageList(String jsonString) {
        try {
            JSONObject poshiks = getJsonObjectFromData(jsonString);
            String poshiksArray = poshiks.getString("favorites");
            JSONArray jsonarray = new JSONArray(poshiksArray);
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Image item = new MarketImage(jsonobject.getInt("id"), jsonobject.getString("extension"), true, false);
                result.add(item);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @NonNull
    public static Category[] getCategories(String jsonString) {
        try {
            JSONObject categories = getJsonObjectFromData(jsonString);
            String categoriesArray = categories.getString("categories");
            JSONArray jsonarray = new JSONArray(categoriesArray);
            Category[] result = new Category[jsonarray.length()];
            for (int i = 0; i < jsonarray.length(); ++i) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                result[i] = new Category();
                result[i].name = jsonobject.getString("name");
                result[i].id = jsonobject.getInt("id");
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return new Category[0];
        }
    }

    @NonNull
    public static String[] getTags(String jsonString) {
        try {
            JSONObject categories = getJsonObjectFromData(jsonString);
            String categoriesArray = categories.getString("tags");
            JSONArray jsonarray = new JSONArray(categoriesArray);
            String[] result = new String[jsonarray.length()];
            for (int i = 0; i < jsonarray.length(); ++i) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                result[i] = jsonobject.getString("value");
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    @NonNull
    public static List<Object> getPurchasedImageList(String jsonString) {
        List<Object> result = new ArrayList<>();
        try {
            JSONObject poshiks = getJsonObjectFromData(jsonString);
            String poshiksArray = poshiks.getString("purchases");
            JSONArray jsonarray = new JSONArray(poshiksArray);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Image item = new MarketImage(jsonobject.getInt("id"), jsonobject.getString("extension"), false, true);
                result.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @NonNull
    public static List<Object> getHandmadeImageList(String jsonString) {
        List<Object> result = new ArrayList<>();
        try {
            JSONObject poshiks = getJsonObjectFromData(jsonString);
            String poshiksArray = poshiks.getString("myPoshiks");
            JSONArray jsonarray = new JSONArray(poshiksArray);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Image item = new HandmadeImage(jsonobject.getInt("id"), jsonobject.getString("extension"));
                result.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @NonNull
    public static String convertEmail(String email) {
        return convertEmailPassword(email, null);
    }

    @NonNull
    public static String convertEmailPassword(String email, String password) {
        JSONObject data = new JSONObject();
        try {
            data.put("email", email);
            if (null != password) {
                data.put("password", password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    @NonNull
    public static String getMessage(String jsonString) {
        String result = "";
        try {
            JSONObject json = new JSONObject(jsonString);
            result = json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getTotalNumPages(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            return json.getJSONObject("pagination").getInt("totalPages");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
