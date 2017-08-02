package ru.jufy.myposh.utils;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ru.jufy.myposh.MyPoshApplication;

/**
 * Created by BorisDev on 31.07.2017.
 */

public class JsonHelper {

    public static int SOCIAL_AUTH_RSP_DATA_IDX = 0;

    /*
    * Message example:
    {
	    "data":
	    {
		    "link":"https:\/\/oauth.vk.com\/authorize?client_id=6089312&redirect_uri=http%3A%2F%2Fkulon.jwma.ru%2Fapi%2Fv1%2Fsocialite%2Fvkontakte%2Fcallback&scope=email&response_type=code"
	    },
	    "message":"Link was obtained successfully."
    }
    */

    public static String getSocialAuthLink(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray nameArray = json.names();
            JSONArray valArray = json.toJSONArray(nameArray);
            JSONObject linkArr = valArray.getJSONObject(SOCIAL_AUTH_RSP_DATA_IDX);

            return linkArr.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static KulonToken getToken(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray nameArray = json.names();
            JSONArray valArray = json.toJSONArray(nameArray);
            JSONObject linkArr = valArray.getJSONObject(SOCIAL_AUTH_RSP_DATA_IDX);

            Date expDate = stringToDate(linkArr.getString("update_before"), "yyyy-MM-dd HH:mm:ss");
            return new KulonToken(linkArr.getString("token"), expDate);
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
            //data.put("email", "kayashovak@gmail.com");
            //data.put("password", "katyakv");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
