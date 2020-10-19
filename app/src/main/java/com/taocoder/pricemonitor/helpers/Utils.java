package com.taocoder.pricemonitor.helpers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class Utils {

    public static void toastMessage(Context context, String message) {
        if (context != null)
            Toasty.info(context, message).show();
    }

    public static void hideKeyboard(Context context, View view) {
        if (view == null) return;
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static long daysAgo(String passDate) {

        long days = 0;
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        try {

           if (passDate != null) {
               Date past = simpleDateFormat.parse(passDate);
               long seconds=TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
               long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
               long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
               days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
           }
            return days;
        }
        catch (Exception e) {
            Log.i("@@RES", "E: " + e.getMessage());
        }
        return days;
    }
}