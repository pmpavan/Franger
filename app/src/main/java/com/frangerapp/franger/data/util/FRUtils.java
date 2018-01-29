package com.frangerapp.franger.data.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Pavan on 24/01/18.
 */

public class FRUtils {

    /**
     * Returns the SharedPreferences using the package name as shared preference
     * name
     *
     * @param ctx
     * @return
     */
    public static SharedPreferences getPrefs(Context ctx) {
        return ctx.getSharedPreferences(ctx.getPackageName(),
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getPrefs(Context ctx, String preferenceKey) {
        return ctx.getSharedPreferences(preferenceKey,
                Context.MODE_PRIVATE);
    }

    public static void storeInPreferences(Context context, String preferenceKey, String key,
                                          String value) {
        SharedPreferences sharedPreferences = getPrefs(context, preferenceKey);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void removeFromPreferences(Context context, String preferenceKey, String key) {
        SharedPreferences sharedPreferences = getPrefs(context, preferenceKey);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void storeInPreferences(Context context, String preferenceKey, String key,
                                          boolean value) {
        SharedPreferences sharedPreferences = getPrefs(context, preferenceKey);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void storeInPreferences(Context context, String preferenceKey, String key, int value) {
        SharedPreferences sharedPreferences = getPrefs(context, preferenceKey);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void storeInPreferences(Context context, String preferenceKey, String key, long value) {
        SharedPreferences sharedPreferences = getPrefs(context, preferenceKey);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

}