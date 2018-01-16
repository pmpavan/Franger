package com.franger.mobile.logger;

import android.util.Log;

/**
 * Created by Pavan on 10/11/17.
 */

public class FRLogger {

    public static boolean loggerSwitch = false;
    private static String LOGGER_TAG = "FR_LOGGER";

    public FRLogger() {
    }

    /**
     * @param status
     */
    public static void setSwitch(boolean status) {
        loggerSwitch = status;
    }

    /**
     * @param msg Log as Debug
     */
    public static void msg(String msg) {
        if (loggerSwitch) {
            Log.d(LOGGER_TAG, msg);
        }
    }


    /**
     * @param error Log as Error
     */
    public static void error(String error) {
        if (loggerSwitch) {
            Log.e(LOGGER_TAG, error);
        }
    }

    /**
     * @param msg Log as Debug only when app is debug
     */
    public static void msg(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * @param error Log as Error only when app is debug
     */
    public static void error(String tag, String error) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, error);
        }
    }

    public static void logInformation(final String tag, final String message) {
        //if (BuildConfig.DEBUG) {
        Log.i(tag, message);
        //}
    }

    public static void logError(final String tag, final String errorMessage) {
        //if (BuildConfig.DEBUG) {
        Log.e(tag, errorMessage);
        //}
    }

    public static void logError(final String tag, final Throwable error) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, error);
        }
    }
}
