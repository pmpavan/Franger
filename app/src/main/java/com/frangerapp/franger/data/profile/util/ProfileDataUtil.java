package com.frangerapp.franger.data.profile.util;

import android.content.Context;

import com.frangerapp.franger.data.profile.model.ContactSyncRequest;
import com.frangerapp.franger.data.profile.model.ContactSyncRequestData;
import com.frangerapp.franger.data.profile.model.ProfileDataRequest;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pavan on 23/01/18.
 */

public class ProfileDataUtil {

    public static String getEditProfileUrl(String userId) {
        String builder = ProfileDataConstants.EDIT_PROFILE_URL;
        return String.format(builder, userId);
    }

    public static String getContactsSyncUrl(String userId) {
        String builder = ProfileDataConstants.SYNC_CONTACTS_URL;
        return String.format(builder, userId);
    }

    public static String getProfileRequestObject(Gson gson, String userName) {
        ProfileDataRequest request = new ProfileDataRequest();
        request.setName(userName);
        return gson.toJson(request);
    }

    public static String getContactSyncRequestObject(Gson gson, List<String> phoneNumberHashMap) {
        ContactSyncRequest request = new ContactSyncRequest();
        List<ContactSyncRequestData> contactSyncRequestDataList = new ArrayList<>();
        if (phoneNumberHashMap != null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            ContactSyncRequestData contactSyncRequestData = new ContactSyncRequestData();
            for (String phoneNumber : phoneNumberHashMap) {
                contactSyncRequestData.setOriginalNumber(phoneNumber);
                try {
                    Phonenumber.PhoneNumber cleanedPhoneNumber = phoneUtil.parse(phoneNumber, "IN");
                    String number = "";
                    if (cleanedPhoneNumber.hasNationalNumber()) {
                        number = String.valueOf(cleanedPhoneNumber.getNationalNumber());
                    }
                    contactSyncRequestData.setPureNumber(number);
                } catch (NumberParseException e) {
                    contactSyncRequestData.setPureNumber(phoneNumber);
                }

            }
        }
        request.setData(contactSyncRequestDataList);
        return gson.toJson(request);
    }

}
