package com.frangerapp.franger.data.common.util;

import android.util.Base64;

import com.frangerapp.franger.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pavan on 01/02/18.
 */

public class DataUtil {

    public static Map<String, String> getCommonApiHeaders() {
        return composeHeaders(null);
    }

    public static Map<String, String> getCommonApiHeaders(String authToken) {
        return composeHeaders(authToken);
    }

    private static Map<String, String> composeHeaders(final String authorizationToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put(DataConstants.API_HEADER_KEY_ACCEPT, DataConstants.API_HEADER_VALUE_ACCEPT);
        headers.put(DataConstants.API_HEADER_KEY_USER_AGENT, DataConstants.API_HEADER_VALUE_USER_AGENT);
        if (authorizationToken != null && !authorizationToken.isEmpty()) {
            String basicAuthorizationToken = "Basic " + Base64.encodeToString(authorizationToken.getBytes(), Base64.NO_WRAP);
            headers.put(DataConstants.API_HEADER_KEY_AUTHORIZATION, basicAuthorizationToken);
        }
        JSONObject requestId = new JSONObject();
        try {
            requestId.put(DataConstants.API_HEADER_KEY_MOBILE_TYPE, DataConstants.API_HEADER_VALUE_MOBILE_TYPE);
            requestId.put(DataConstants.API_HEADER_KEY_DEVICE_DESC, android.os.Build.MODEL);
            requestId.put(DataConstants.API_HEADER_KEY_OS_VERSION, android.os.Build.VERSION.RELEASE);
            requestId.put(DataConstants.API_HEADER_KEY_APP_VERSION, BuildConfig.VERSION_NAME);
            requestId.put(DataConstants.API_HEADER_KEY_API_VERSION, DataConstants.API_HEADER_VALUE_API_VERSION);
        } catch (JSONException exp) {

        }
        headers.put(DataConstants.API_HEADER_KEY_REQUEST_ID, requestId.toString());
        return headers;
    }
}
