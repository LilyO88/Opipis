package com.lidorttol.opipis.utils;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.RadioButton;
import android.widget.RatingBar;

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Modifications

    public static boolean isValidString(String string) {
        return !TextUtils.isEmpty(string);
    }

    public static boolean isValidRatingBar(RatingBar ratingBar, float min, float max) {
        return (ratingBar.getRating() > min && ratingBar.getRating() <= max);
    }

    public static boolean isValidRadiobutton(RadioButton yes, RadioButton no) {
        return (yes.isChecked() || no.isChecked());
    }



}
