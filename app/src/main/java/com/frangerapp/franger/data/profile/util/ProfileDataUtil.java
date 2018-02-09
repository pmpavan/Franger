package com.frangerapp.franger.data.profile.util;

import com.frangerapp.franger.data.profile.model.ProfileDataRequest;
import com.google.gson.Gson;

/**
 * Created by Pavan on 23/01/18.
 */

public class ProfileDataUtil {

    public static String getEditProfileUrl(String userId) {
        String builder = ProfileDataConstants.EDIT_PROFILE_URL;
        return String.format(builder, userId);
    }

    public static String getProfileRequestObject(Gson gson, String userName) {
        ProfileDataRequest request = new ProfileDataRequest();
        request.setName(userName);
        return gson.toJson(request);
    }

}
