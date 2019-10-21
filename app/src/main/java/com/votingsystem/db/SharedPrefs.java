package com.votingsystem.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.votingsystem.application.VotingSystem;
import com.votingsystem.util.SharedPreferencesConstants;
import com.votingsystem.util.StringConstants;

public class SharedPrefs {

    private static SharedPrefs instance;

    public static SharedPrefs getInstance() {
        if (instance == null)
            instance = new SharedPrefs();
        return instance;
    }

    private SharedPreferences preferences;

    private SharedPrefs() {
        preferences = VotingSystem.getInstance().getSharedPreferences(SharedPreferencesConstants.SHAREDPREF, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(SharedPreferencesConstants.IS_LOGGED_IN, false);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        preferences.edit().putBoolean(SharedPreferencesConstants.IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isAdmin() {
        return preferences.getBoolean(SharedPreferencesConstants.IS_ADMIN, false);
    }

    public void setAdmin(boolean isAdmin) {
        preferences.edit().putBoolean(SharedPreferencesConstants.IS_ADMIN, isAdmin).apply();
    }


    public String getId() {
        return preferences.getString(SharedPreferencesConstants.VOTER_ID, "");
    }

    public void setId(String id) {
        preferences.edit().putString(SharedPreferencesConstants.VOTER_ID, id).apply();
    }

    public void setFingerprintEnrolled(boolean enrolled){
        preferences.edit().putBoolean(SharedPreferencesConstants.FINGER_ENROLLED, enrolled).apply();
    }

    public boolean hasFingerprintEnrolled(){
        return preferences.getBoolean(SharedPreferencesConstants.FINGER_ENROLLED, false);
    }

    public void clear(){
        preferences.edit().clear().apply();
    }
}
