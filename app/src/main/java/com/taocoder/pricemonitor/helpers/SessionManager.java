package com.taocoder.pricemonitor.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String NAME = "priceMonitorSessionPreferences";
    private static SessionManager instance;

    //Fields
    private static final String email = "email";
    private static final String type = "type";
    private static final String name = "name";
    private static final String first = "first";

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }

        return instance;
    }

    public void setEmail(String value) {
        editor.putString(email, value);
        editor.apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(email, "");
    }

    public void setType(String value) {
        editor.putString(type, value);
        editor.apply();
    }

    public String getType() {
        return sharedPreferences.getString(type, "");
    }

    public void setName(String value) {
        editor.putString(name, value);
        editor.apply();
    }

    public String getName() {
        return sharedPreferences.getString(name, "");
    }

    public void setIsFirstTime(boolean value) {
        editor.putBoolean(first, value);
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(first, true);
    }
}
