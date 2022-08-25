package com.fearmygaze.mApp.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class PrivatePreference {

    private final SharedPreferences sharedPreferences;

    public PrivatePreference(Context context) {
        sharedPreferences = context.getSharedPreferences("mApp", Context.MODE_PRIVATE);
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

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
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

    public Map<String, ?> getAll(){
        return sharedPreferences.getAll();
    }
}