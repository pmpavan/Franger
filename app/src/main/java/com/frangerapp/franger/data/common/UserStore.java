package com.frangerapp.franger.data.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.frangerapp.franger.data.common.util.DataConstants;
import com.frangerapp.franger.data.util.FRUtils;
import com.frangerapp.franger.domain.user.model.User;
import com.google.gson.Gson;

/**
 * Created by Pavan on 24/01/18.
 */

public class UserStore {

    private static final String PREFERENCE = "user_preference";

    private SharedPreferences preferences;
    private Gson gson;

    Object lock = new Object();

    public UserStore(Context context, Gson gson) {
        this.gson = gson;
        preferences = FRUtils.getPrefs(context, PREFERENCE);
    }

    public enum PREFERENCE_TYPE {

        AUTH_TOKEN("auth_token"),
        USER_NAME("user_name"),
        PHONE_NUMBER("phone_number"),
        USER_ID("access_token");
        String key;

        PREFERENCE_TYPE(String key) {
            this.key = key;
        }
    }

    public void clearPrefs(Context context) {
        for (PREFERENCE_TYPE type : PREFERENCE_TYPE.values()) {
            FRUtils.removeFromPreferences(context, PREFERENCE, type.key);
        }
    }

    public String getAuthToken(Context context, String defValue) {
        return preferences.getString(PREFERENCE_TYPE.AUTH_TOKEN.key, defValue);
    }

    public void storeAuthToken(Context context, String appToken) {
        FRUtils.storeInPreferences(context, PREFERENCE, PREFERENCE_TYPE.AUTH_TOKEN.key, appToken);
    }

    public void storeUserName(Context context, String userName) {
        FRUtils.storeInPreferences(context, PREFERENCE, PREFERENCE_TYPE.USER_NAME.key, userName);
    }

    public String getUserName(Context context, String defValue) {
        return preferences.getString(PREFERENCE_TYPE.USER_NAME.key, defValue);
    }

    public void storeUserId(Context context, String userId) {
        FRUtils.storeInPreferences(context, PREFERENCE, PREFERENCE_TYPE.USER_ID.key, userId);
    }

    public String getUserId(Context context, String defValue) {
        return preferences.getString(PREFERENCE_TYPE.USER_ID.key, defValue);
    }

    @Nullable
    public User getUser() {
        String userId = preferences.getString(PREFERENCE_TYPE.USER_ID.key, DataConstants.EMPTY);
        String userName = preferences.getString(PREFERENCE_TYPE.USER_NAME.key, DataConstants.EMPTY);
        String phoneNumber = preferences.getString(PREFERENCE_TYPE.PHONE_NUMBER.key, DataConstants.EMPTY);
        String authToken = preferences.getString(PREFERENCE_TYPE.AUTH_TOKEN.key, DataConstants.EMPTY);
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(authToken)) {
            return new User(userId, userName, phoneNumber, authToken);
        } else {
            return null;
        }
    }

    public void saveUser(@NonNull User user) {
        synchronized (lock) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREFERENCE_TYPE.PHONE_NUMBER.key, user.getPhoneNumber());
            editor.putString(PREFERENCE_TYPE.USER_ID.key, user.getUserId());
            editor.putString(PREFERENCE_TYPE.USER_NAME.key, user.getUserName());
            editor.putString(PREFERENCE_TYPE.AUTH_TOKEN.key, user.getAuthToken());
            editor.commit();
        }
    }
}