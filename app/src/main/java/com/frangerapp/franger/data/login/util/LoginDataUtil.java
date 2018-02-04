package com.frangerapp.franger.data.login.util;

import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.util.DataConstants;
import com.frangerapp.franger.data.login.model.LoginRequestData;
import com.google.gson.Gson;

/**
 * Created by Pavan on 23/01/18.
 */

public class LoginDataUtil {

    @NonNull
    public static String getLoginUrl() {
        return new StringBuilder(DataConstants.PROTOCOL)
                .append(LoginDataConstants.BASE_DOMAIN_URL)
                .append(LoginDataConstants.SIGN_UP_URL).toString();
    }

    public static String getVerifyUrl(String phoneNumber) {
        String builder = DataConstants.PROTOCOL + LoginDataConstants.BASE_DOMAIN_URL + LoginDataConstants.VERIFY_URL;
        return String.format(builder, phoneNumber);
    }

    public static String getLoginRequestObject(Gson gson, String username, String countryCode, String phoneNumber) {
        LoginRequestData loginRequestData = new LoginRequestData();
        loginRequestData.setName(username);
        loginRequestData.setCountryCode(countryCode);
        loginRequestData.setNumber(phoneNumber);
        return gson.toJson(loginRequestData);
    }
}
