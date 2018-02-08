package com.frangerapp.franger.data.profile.util;

/**
 * Created by Pavan on 23/01/18.
 */

public class ProfileDataUtil {

    public static String getEditProfileUrl(String userId) {
        String builder = ProfileDataConstants.EDIT_PROFILE_URL;
        return String.format(builder, userId);
    }

}
