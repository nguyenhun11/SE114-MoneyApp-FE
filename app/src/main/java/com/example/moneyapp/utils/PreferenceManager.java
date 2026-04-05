package com.example.moneyapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static String PREF_NAME = "MoneyAppPrefs";
    private static String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static SharedPreferences sharedPreferences;
    private static PreferenceManager instance;
    public static synchronized PreferenceManager getInstance(Context context){
        if (instance == null){
            instance = new PreferenceManager();
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public void setLoggedIn(boolean loggedIn){
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }
    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUserID(String userID){
        sharedPreferences.edit().putString("userID", userID).apply();
    }
    public String getUserID(){
        return sharedPreferences.getString("userID", null);
    }

    public void clear(){
        sharedPreferences.edit().clear().apply();
    }

}
