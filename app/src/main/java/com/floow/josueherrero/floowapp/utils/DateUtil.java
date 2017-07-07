package com.floow.josueherrero.floowapp.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

/**
 * Created by JosuéManuel on 05/07/2017.
 *
 * This is a util to format the date
 */

public final class DateUtil {

    @SuppressLint({"SimpleDateFormat"})
    public static String formatDate(final long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy - HH:mm");
        return  dateFormat.format(date);
    }

}
