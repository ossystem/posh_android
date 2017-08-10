package ru.jufy.myposh.utils;

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
import ru.jufy.myposh.data.Image;
import ru.jufy.myposh.data.MarketImage;

/**
 * Created by BorisDev on 31.07.2017.
 */

public class JsonHelper {

    public static int SOCIAL_AUTH_RSP_DATA_IDX = 0;

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
        JSONArray nameArray = json.names();
        JSONArray valArray = json.toJSONArray(nameArray);
        return valArray.getJSONObject(SOCIAL_AUTH_RSP_DATA_IDX);
    }

    public static KulonToken getToken(String jsonString) {
        try {
            JSONObject tokenData = getJsonObjectFromData(jsonString);
            Date expDate = stringToDate(tokenData.getString("update_before"), "yyyy-MM-dd HH:mm:ss");
            return new KulonToken(tokenData.getString("token"), expDate);
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
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;
    }

    public static String convertTokenToJson() {
        JSONObject data = new JSONObject();
        try {
            data.put("token", MyPoshApplication.getCurrentToken().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    public static List<Image> getMarketImageList(String jsonString) {
        try {
            JSONObject poshiks = getJsonObjectFromData(jsonString);
            String poshiksArray = poshiks.getString("marketPoshiks");
            JSONArray jsonarray = new JSONArray(poshiksArray);
            List<Image> result = new ArrayList<>();
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Image item = new MarketImage();
                item.id = jsonobject.getInt("id");
                item.isFavorite = jsonobject.getBoolean("is_favorite");
                item.isPurchased = jsonobject.getBoolean("is_purchased");
                result.add(item);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Image> getFavoritesImageList(String jsonString) {
        try {
            JSONObject poshiks = getJsonObjectFromData(jsonString);
            String poshiksArray = poshiks.getString("favorites");
            JSONArray jsonarray = new JSONArray(poshiksArray);
            List<Image> result = new ArrayList<>();
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                Image item = new MarketImage();
                item.id = jsonobject.getInt("id");
                item.isFavorite = true;
                item.isPurchased = false;
                result.add(item);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getCategories(String jsonString) {
        try {
            JSONObject categories = getJsonObjectFromData(jsonString);
            String categoriesArray = categories.getString("categories");
            JSONArray jsonarray = new JSONArray(categoriesArray);
            String[] result = new String[jsonarray.length()];
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                result[i] = jsonobject.getString("name");
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return new String[0];
        }
    }
}
