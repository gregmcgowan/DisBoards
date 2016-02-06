package com.drownedinsound.data;

import android.content.SharedPreferences;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by gmcgowan on 21/07/2015.
 */
@Singleton
public class AppPreferences {

    private SharedPreferences sharedPreferences;

    @Inject
    public AppPreferences(@Named("AppState") SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean containsKey(String key) {
        return sharedPreferences.contains(key);
    }

    public String getStringSharedPreference(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setStringSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean getBooleanSharedPreference(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void setBooleanSharedPreference(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public long getLongSharedPreference(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public long getLongSharedPreference(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public void setLongSharedPreferences(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public int getIntSharedPreference(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void setIntSharedPreferences(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void clearSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

