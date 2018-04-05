package com.frangerapp.franger.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.frangerapp.franger.data.util.FRUtils;
import com.google.gson.Gson;

public class ChatStore {


    private static final String PREFERENCE = "chat_preference";

    private SharedPreferences preferences;
    private Gson gson;

    private final Object lock = new Object();

    public ChatStore(Context context, Gson gson) {
        this.gson = gson;
        preferences = FRUtils.getPrefs(context, PREFERENCE);
    }

    public enum PREFERENCE_TYPE {

        RANDOM_NAMES("random_names");
        public String key;

        PREFERENCE_TYPE(String key) {
            this.key = key;
        }
    }

    public void clearPrefs(Context context) {
        for (PREFERENCE_TYPE type : PREFERENCE_TYPE.values()) {
            FRUtils.removeFromPreferences(context, PREFERENCE, type.key);
        }
    }

    public String getRandomNames(Context context, String defValue) {
        return preferences.getString(PREFERENCE_TYPE.RANDOM_NAMES.key, defValue);
    }

    public void storeRandomNames(Context context, String appToken) {
        FRUtils.storeInPreferences(context, PREFERENCE, PREFERENCE_TYPE.RANDOM_NAMES.key, appToken);
    }
}
