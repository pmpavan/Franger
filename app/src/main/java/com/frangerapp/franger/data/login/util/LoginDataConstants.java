package com.frangerapp.franger.data.login.util;

import com.frangerapp.franger.BuildConfig;

/**
 * Created by Pavan on 23/01/18.
 */

public class LoginDataConstants {


    public static final String BASE_DOMAIN_URL;

    static {
        if ("prod".equals(BuildConfig.FLAVOR)) {
            BASE_DOMAIN_URL = "139.59.46.115:8080";
        } else {
            BASE_DOMAIN_URL = "139.59.46.115:8080";
        }
    }

    public static final String SIGN_UP_URL = "/signup";

    public static final String VERIFY_URL = "/users/%s/verify";
}
