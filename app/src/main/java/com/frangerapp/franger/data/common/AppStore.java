package com.frangerapp.franger.data.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by Pavan on 24/01/18.
 */

public class AppStore {

    private SharedPreferences preferences;
    private final Object lock = new Object();

    private static final String APP_PREFERENCES = "APP_PREFERENCES";

    public enum PREFERENCE_TYPE {

        VERSION_CODE("KEY_APP_VERSION_CODE");
        String key;

        PREFERENCE_TYPE(String key) {
            this.key = key;
        }
    }

    public AppStore(@NonNull Context context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public int getAppVersionCode() {
        return preferences.getInt(PREFERENCE_TYPE.VERSION_CODE.key, -1);
    }

    public void setAppVersionCode(int versionCode) {
        synchronized (lock) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(PREFERENCE_TYPE.VERSION_CODE.key, versionCode);
            editor.commit();
        }
    }
}
