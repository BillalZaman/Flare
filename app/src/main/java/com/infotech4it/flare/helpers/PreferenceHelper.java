package com.infotech4it.flare.helpers;

import android.app.Activity;
import android.content.SharedPreferences;

import com.infotech4it.flare.constant.Constant;

/**
 * Created by Bilal Zaman on 10/11/2020.
 */
public class PreferenceHelper {

    public static PreferenceHelper instance_;
    //	private Context context_;
    private SharedPreferences sharedPreferences;

    public PreferenceHelper() {
        sharedPreferences = Constant.CONTEXT.getSharedPreferences(Constant.PREF_NAMES, Activity.MODE_PRIVATE);
    }

    public synchronized static PreferenceHelper getInstance() {
        if (instance_ == null) {
            instance_ = new PreferenceHelper();
        }
        return instance_;
    }

    public void setInt(String key, int val) {
        sharedPreferences.edit().putInt(key, val).apply();

    }

    public int getInt(String key, int defval) {
        return sharedPreferences.getInt(key, defval);
    }

    public void setString(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    public void setAppString(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    public String getAppString(String key, String defval) {
        return sharedPreferences.getString(key, defval);
    }

    public String getString(String key, String defval) {
        return sharedPreferences.getString(key, defval);
    }

    public void setLong(String key, long val) {
        sharedPreferences.edit().putLong(key, val).apply();
    }

    public long getLong(String key, long defval) {
        return sharedPreferences.getLong(key, defval);
    }

    public void setBol(String key, boolean val) {
        sharedPreferences.edit().putBoolean(key, val).apply();
    }

    public boolean getBol(String key, boolean defval) {
        return sharedPreferences.getBoolean(key, defval);
    }

    public void clearAllPreferences() {
        sharedPreferences.edit().clear().apply();

    }

    public void removePreference(String key) {

        sharedPreferences.edit().remove(key).apply();
    }

    public void removeall() {

        sharedPreferences.edit().clear().apply();

    }

}