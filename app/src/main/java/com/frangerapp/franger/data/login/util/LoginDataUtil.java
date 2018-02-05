package com.frangerapp.franger.data.login.util;

import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.util.DataConstants;
import com.frangerapp.franger.data.login.model.LoginRequestData;
import com.frangerapp.franger.data.login.model.LoginVerifyRequestData;
import com.google.gson.Gson;

/**
 * Created by Pavan on 23/01/18.
 */

public class LoginDataUtil {

    @NonNull
    public static String getLoginUrl() {
        return LoginDataConstants.SIGN_UP_URL;
    }

    public static String getVerifyUrl(String userId) {
        String builder = LoginDataConstants.VERIFY_URL;
        return String.format(builder, userId);
    }

    public static String getLoginRequestObject(Gson gson, String username, String countryCode, String phoneNumber) {
        LoginRequestData loginRequestData = new LoginRequestData();
        loginRequestData.setName(username);
        loginRequestData.setCountryCode(countryCode);
        loginRequestData.setNumber(phoneNumber);
        return gson.toJson(loginRequestData);
    }

    public static String getLoginVerifyRequestData(Gson gson, String userEnteredOtp) {
        LoginVerifyRequestData loginVerifyRequestData = new LoginVerifyRequestData();
        loginVerifyRequestData.setUserEnteredOTP(userEnteredOtp);
        return gson.toJson(loginVerifyRequestData);
    }
}
