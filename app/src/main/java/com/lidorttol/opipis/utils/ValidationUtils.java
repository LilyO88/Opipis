package com.lidorttol.opipis.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;


// DO NOT TOUCH

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static boolean isValidUrl(String url) {
        return !TextUtils.isEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }

    public static boolean isValidCIF(String cif) {
        final Pattern patron = Pattern.compile("[ABCDEFGHJKLMNPQRSUVW]{1}[0-9]{7}([0-9A-J]{1})");
        return !TextUtils.isEmpty(cif) && patron.matcher(cif).matches();
    }
    //Modifications

    public static boolean isValidString(String string) {
        return !TextUtils.isEmpty(string);
    }

    public static boolean isValidAddress(String address) {
        return !TextUtils.isEmpty(address);
    }


}
