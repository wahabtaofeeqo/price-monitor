package com.taocoder.pricemonitor.helpers;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
}