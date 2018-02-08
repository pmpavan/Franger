package com.frangerapp.franger.data.contacts.util;

import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.util.DataConstants;

/**
 * Created by Pavan on 23/01/18.
 */

public class ContactDataUtil {

    @NonNull
    public static String getContactUrl() {
        return DataConstants.PROTOCOL +
                DataConstants.BASE_DOMAIN_URL +
                ContactDataConstants.SIGN_UP_URL;
    }

}
