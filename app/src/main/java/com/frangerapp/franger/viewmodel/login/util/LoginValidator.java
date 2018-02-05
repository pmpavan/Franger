package com.frangerapp.franger.viewmodel.login.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.frangerapp.franger.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by Pavan on 24/01/18.
 */

public class LoginValidator {

    public static final int VALIDATION_SUCCESS = -1;

    public boolean validateNumber(String countryCode, String phoneNumberTxt) {
        boolean isValid = false;
        String number = countryCode + phoneNumberTxt;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(number, "IN");
            isValid = phoneUtil.isValidNumber(swissNumberProto);
        } catch (NumberParseException ignored) {
        }
        return isValid;
    }
}
