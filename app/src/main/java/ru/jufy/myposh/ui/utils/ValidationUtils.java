package ru.jufy.myposh.ui.utils;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by rolea on 5/14/2017.
 */

public class ValidationUtils {

    public static boolean isCardHolderNameValid(String cardHolderName){
        if(cardHolderName.length() == 1 && cardHolderName.matches("[ .-]+")) {
            return false;

        } else {
            String buffer = cardHolderName;
            if (!buffer.matches("[a-zA-Z .-]+")) {
                buffer = buffer.replaceAll("[^a-zA-Z .-]+", "");
                return false;

            } else if (buffer.startsWith(" ") || buffer.startsWith("-") || buffer.startsWith(".")) {
                buffer = buffer.substring(1);
                return false;
            }
        }

        return true;
    }

    public static boolean isValidEmail(String email){

        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     *  Check if Russian number is valid
     * @param phoneNumber russian phone number
     * @return true if phone matches with following:
     * +79999999999, +7(999)-999-99-99 +7(999)-999-9999
     * +7999-999-9999, +7(999)9999999, +7-999-999-99-99
     * +7 can be represented by 8 or 7
     * @return false in other cases
     */
    public static boolean isPhoneNumberValid(String phoneNumber)  {
        String pattern = "^(\\d{10})|((\\+7|8|7)?[-]?([\\(]?([0-9]{3})[\\)]?)?[ \\.\\-]?([0-9]{3})[ \\.\\-]?([0-9]{2})[-]?([0-9]{2}))";
        return phoneNumber.matches(pattern);
    }

    public static boolean isEmtyView(EditText view) {
        return view.getText().toString().trim().equals("");
    }
}
