package com.frangerapp.franger.data.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.frangerapp.franger.data.common.util.DataConstants;
import com.frangerapp.franger.data.util.FRUtils;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.google.gson.Gson;

/**
 * Created by Pavan on 24/01/18.
 */

public class UserStore {

    private static final String PREFERENCE = "user_preference";

    private SharedPreferences preferences;
    private Gson gson;

    private final Object lock = new Object();

    public UserStore(Context context, Gson gson) {
        this.gson = gson;
        preferences = FRUtils.getPrefs(context, PREFERENCE);
    }

    public enum PREFERENCE_TYPE {

        AUTH_TOKEN("auth_token"),
        USER_NAME("user_name"),
        PHONE_NUMBER("phone_number"),
        COUNTRY_CODE("country_code"),
        IS_USER_VERIFIED("is_user_verified"),
        IS_USER_ONBOARDING_COMPLTD("is_user_onboarding_completed"),
        IS_USER_PROFILE_COLLECTED("is_user_profile_completed"),
        USER_ID("user_id");
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

    public void setUserVerified(Context context, boolean isVerified) {
        FRUtils.storeInPreferences(context, PREFERENCE, PREFERENCE_TYPE.IS_USER_VERIFIED.key, isVerified);
    }

    private boolean getIfUserVerified(Context context, boolean defValue) {
        return preferences.getBoolean(PREFERENCE_TYPE.IS_USER_VERIFIED.key, defValue);
    }

    public boolean getIfUserVerified(Context context) {
        return getIfUserVerified(context, false);
    }

    public void setUserOnboarded(Context context, boolean isVerified) {
        FRUtils.storeInPreferences(context, PREFERENCE, PREFERENCE_TYPE.IS_USER_ONBOARDING_COMPLTD.key, isVerified);
    }

    private boolean getIfUserOnboarded(Context context, boolean defValue) {
        return preferences.getBoolean(PREFERENCE_TYPE.IS_USER_ONBOARDING_COMPLTD.key, defValue);
    }

    public boolean getIfUserOnboarded(Context context) {
        return getIfUserOnboarded(context, false);
    }

    public void setUserProfileCollected(Context context, boolean isVerified) {
        FRUtils.storeInPreferences(context, PREFERENCE, PREFERENCE_TYPE.IS_USER_PROFILE_COLLECTED.key, isVerified);
    }

    public boolean getIfUserProfileCollected(Context context, boolean defValue) {
        return preferences.getBoolean(PREFERENCE_TYPE.IS_USER_PROFILE_COLLECTED.key, defValue);
    }

    public boolean getIfUserProfileCollected(Context context) {
        return getIfUserProfileCollected(context, false);
    }

    public void storeUserId(Context context, String userId) {
        FRUtils.storeInPreferences(context, PREFERENCE, PREFERENCE_TYPE.USER_ID.key, userId);
    }

    public String getUserId(Context context, String defValue) {
        return preferences.getString(PREFERENCE_TYPE.USER_ID.key, defValue);
    }

    public String getPhoneNumber(Context context, String defValue) {
        return preferences.getString(PREFERENCE_TYPE.PHONE_NUMBER.key, defValue);
    }

    public String getCountryCode(Context context, String defValue) {
        return preferences.getString(PREFERENCE_TYPE.COUNTRY_CODE.key, defValue);
    }


    @Nullable
    public LoggedInUser getUser() {
        String userId = preferences.getString(PREFERENCE_TYPE.USER_ID.key, DataConstants.EMPTY);
        String userName = preferences.getString(PREFERENCE_TYPE.USER_NAME.key, DataConstants.EMPTY);
        String phoneNumber = preferences.getString(PREFERENCE_TYPE.PHONE_NUMBER.key, DataConstants.EMPTY);
        String countryCode = preferences.getString(PREFERENCE_TYPE.COUNTRY_CODE.key, DataConstants.EMPTY);
        String authToken = preferences.getString(PREFERENCE_TYPE.AUTH_TOKEN.key, DataConstants.EMPTY);
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(countryCode) && !TextUtils.isEmpty(authToken)) {
            return new LoggedInUser(userId, userName, countryCode, phoneNumber, authToken);
        } else {
            return null;
        }
    }

    public void savePhoneNumber(String countryCode, String phoneNumber) {
        synchronized (lock) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREFERENCE_TYPE.PHONE_NUMBER.key, phoneNumber);
            editor.putString(PREFERENCE_TYPE.COUNTRY_CODE.key, countryCode);
            editor.commit();
        }
    }

    public void saveUser(@NonNull LoggedInUser loggedInUser) {
        synchronized (lock) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREFERENCE_TYPE.PHONE_NUMBER.key, loggedInUser.getPhoneNumber());
            editor.putString(PREFERENCE_TYPE.COUNTRY_CODE.key, loggedInUser.getCountryCode());
            editor.putString(PREFERENCE_TYPE.USER_ID.key, loggedInUser.getUserId());
            editor.putString(PREFERENCE_TYPE.USER_NAME.key, loggedInUser.getUserName());
            editor.putString(PREFERENCE_TYPE.AUTH_TOKEN.key, loggedInUser.getAuthToken());
            editor.commit();
        }
    }
}