package com.frangerapp.franger.data.common.util;

import com.frangerapp.franger.BuildConfig;

/**
 * Created by Pavan on 23/01/18.
 */

public class DataConstants {

    public static final String BASE_DOMAIN_URL;

    static {
        if ("prod".equals(BuildConfig.FLAVOR)) {
            BASE_DOMAIN_URL = "139.59.46.115:8080";
        } else {
            BASE_DOMAIN_URL = "139.59.46.115:8080";
        }
    }


    public static final String API_HEADER_KEY_ACCEPT = "Accept";
    public static final String API_HEADER_VALUE_ACCEPT = "application/json";
    public static final String API_HEADER_KEY_USER_AGENT = "User-Agent";
    public static final String API_HEADER_VALUE_USER_AGENT = "Franger_Native_Android";
    public static final String API_HEADER_KEY_AUTHORIZATION = "Authorization";
    public static final String API_HEADER_KEY_MOBILE_TYPE = "mobile_type";
    public static final String API_HEADER_VALUE_MOBILE_TYPE = "1";
    public static final String API_HEADER_KEY_DEVICE_DESC = "device_desc";
    public static final String API_HEADER_KEY_OS_VERSION = "os_version";
    public static final String API_HEADER_KEY_APP_VERSION = "app_version";
    public static final String API_HEADER_KEY_API_VERSION = "api_version";
    public static final String API_HEADER_VALUE_API_VERSION = "1";
    public static final String API_HEADER_KEY_DOMAIN_NAME = "domain_name";
    public static final String API_HEADER_KEY_REQUEST_ID = "Request-Id";
    public static final String PROTOCOL = "http://";
    public static final String EMPTY = "";

}
