package com.frangerapp.franger.data.login.util;

import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.util.DataConstants;

/**
 * Created by Pavan on 23/01/18.
 */

public class LoginDataUtil {

    public static String getVerifyUrl(@NonNull String domain, String phoneNumber) {
        String builder = DataConstants.PROTOCOL + domain + LoginDataConstants.verifyUrl;
        return String.format(builder, phoneNumber);
    }
}
