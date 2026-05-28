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
    private static final String KEY_USER_SYNCED = "userSynced";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_AVATAR = "userAvatar";
    private static final String KEY_USER_STREAK = "userDailyStreak";
    private static final String KEY_USER_CHECKED_IN = "userTodayCheckedIn";

    private static SharedPreferences sharedPreferences;
    private static PreferenceManager instance;

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager();
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public User getCurrentUser(){
        if (!isLoggedIn()) return null;
        return new User(
                getUserID(),
                getUserName(),
                getUserEmail(),
                getUserPhone(),
                getUserAvatar(),
                getUserDailyStreak(),
                isUserTodayCheckedIn()
        );
    }
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public void setUserSynced(boolean synced) {
        sharedPreferences.edit().putBoolean(KEY_USER_SYNCED, synced).apply();
    }
    public boolean isUserSynced() { return sharedPreferences.getBoolean(KEY_USER_SYNCED, false); }

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

    public void setUserName(String userName) {
        sharedPreferences.edit().putString(KEY_USER_NAME, userName).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public void setUserEmail(String userEmail) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, userEmail).apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void setUserPhone(String userPhone) {
        sharedPreferences.edit().putString(KEY_USER_PHONE, userPhone).apply();
    }

    public String getUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, null);
    }

    public void setUserAvatar(String userAvatar) {
        sharedPreferences.edit().putString(KEY_USER_AVATAR, userAvatar).apply();
    }

    public String getUserAvatar() {
        return sharedPreferences.getString(KEY_USER_AVATAR, null);

    }
    public void setUserDailyStreak(int userDailyStreak) {
        sharedPreferences.edit().putInt(KEY_USER_STREAK, userDailyStreak).apply();
    }
    public int getUserDailyStreak() {
        return sharedPreferences.getInt(KEY_USER_STREAK, 0);
    }
    public void setUserTodayCheckedIn(boolean userTodayCheckedIn) {
        sharedPreferences.edit().putBoolean(KEY_USER_CHECKED_IN, userTodayCheckedIn).apply();
    }
    public boolean isUserTodayCheckedIn() {
        return sharedPreferences.getBoolean(KEY_USER_CHECKED_IN, false);
    }

}
