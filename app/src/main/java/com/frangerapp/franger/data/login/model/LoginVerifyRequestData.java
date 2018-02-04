package com.frangerapp.franger.data.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavan on 29/01/18.
 */

public class LoginVerifyRequestData {

    public LoginVerifyRequestData() {

    }

    @SerializedName("userEnteredOTP")
    @Expose
    private String userEnteredOTP;

    public String getUserEnteredOTP() {
        return userEnteredOTP;
    }

    public void setUserEnteredOTP(String userEnteredOTP) {
        this.userEnteredOTP = userEnteredOTP;
    }
}
