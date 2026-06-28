package com.example.moneyapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.moneyapp.model.User;

public class PreferenceManager {
    private static final String PREF_NAME = "MoneyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userID";
    private static final String KEY_TOKEN = "authToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_DEFAULT_CURRENCY = "defaultCurrency";
    private static final String KEY_LAST_TAB_TYPE = "lastTabType";
    private static final String KEY_LAST_ACCOUNT_TAB = "lastAccountTab"; // Biến nhớ riêng cho category
    private static final String KEY_LAST_HOME_TAB = "lastHomeTab";


    private static SharedPreferences sharedPreferences;
    private static PreferenceManager instance;

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager();
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public void setLoggedIn(boolean loggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUserID(String userID) {
        sharedPreferences.edit().putString(KEY_USER_ID, userID).apply();
    }

    public String getUserID() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void setRefreshToken(String refreshToken) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }


    public void setUserEmail(String userEmail) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, userEmail).apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }
    public void setDefaultCurrency(String currencyCode) {
        sharedPreferences.edit().putString(KEY_DEFAULT_CURRENCY, currencyCode).apply();
    }

    public String getDefaultCurrency() {
        return sharedPreferences.getString(KEY_DEFAULT_CURRENCY, "VND");
    }
    public void setLastTabType(int type) {
        sharedPreferences.edit().putInt(KEY_LAST_TAB_TYPE, type).apply();
    }
    public int getLastTabType() {
        return sharedPreferences.getInt(KEY_LAST_TAB_TYPE, 0);
    }
    public void setLastAccountTab(int tabIndex) {
        sharedPreferences.edit().putInt(KEY_LAST_ACCOUNT_TAB, tabIndex).apply();
    }
    public int getLastAccountTab() {
        return sharedPreferences.getInt(KEY_LAST_ACCOUNT_TAB, 0); // Mặc định là 0 (Tài khoản)
    }
    public void setLastHomeTab(int tabIndex) {
        sharedPreferences.edit().putInt(KEY_LAST_HOME_TAB, tabIndex).apply();
    }

    public void setNotificationListenerEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean("isNotificationListenerEnabled", enabled).apply();
    }

    public boolean isNotificationListenerEnabled() {
        return sharedPreferences.getBoolean("isNotificationListenerEnabled", false);
    }
}

