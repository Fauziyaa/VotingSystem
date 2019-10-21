package com.votingsystem.util;

import android.content.Context;
import android.text.TextUtils;

import com.votingsystem.db.SharedPrefs;
import com.votingsystem.ui.MainActivity;

import java.util.Calendar;

public class CommonUtil {

    public static void logout(Context context) {
        SharedPrefs.getInstance().clear();
        MainActivity.start(context);
    }

    public static Calendar getDate(String date) {
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(date) && date.contains("/")) {
            String[] startingDateArray = date.split("/");
            calendar.set(Calendar.YEAR, Integer.parseInt(startingDateArray[2]));
            calendar.set(Calendar.MONTH, Integer.parseInt(startingDateArray[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startingDateArray[0]));
        }
        return calendar;
    }

}
