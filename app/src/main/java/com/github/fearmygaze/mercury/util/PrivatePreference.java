package com.github.fearmygaze.mercury.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.fearmygaze.mercury.R;

import java.util.Map;

public class PrivatePreference {

    /*
    * TODO:
    *       We need to make a system to check how many users we have inside the app
    *       so we can exchange the active userID with the other when it is need it
    * */

    private final SharedPreferences sharedPreferences;

    public PrivatePreference(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value).apply();
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value).apply();
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    public void clearValue(String value){
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.remove(value).apply();
    }

    public void clearAllValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public Boolean contains(String value) {
        return sharedPreferences.contains(value);
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }
}