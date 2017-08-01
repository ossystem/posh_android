package ru.jufy.myposh.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BorisDev on 31.07.2017.
 */

public class JsonParser {

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

            KulonToken result = new KulonToken();
            result.token = linkArr.getString("token");
            result.date = linkArr.getString("update_before");

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return new KulonToken();
        }
    }
}
