package com.frangerapp.franger.data.profile.util;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.contacts.Contact;
import com.frangerapp.franger.data.profile.model.ContactSyncRequest;
import com.frangerapp.franger.data.profile.model.ContactSyncRequestData;
import com.frangerapp.franger.data.profile.model.ProfileDataRequest;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public static String getContactSyncRequestObject(Gson gson, List<Contact> phoneNumberHashMap, boolean isLastPage) {
        ContactSyncRequest request = new ContactSyncRequest();
        request.setLastPage(isLastPage);
        List<ContactSyncRequestData> contactSyncRequestDataList = new ArrayList<>();
        if (phoneNumberHashMap != null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            for (Contact phoneNumber : phoneNumberHashMap) {
                ContactSyncRequestData contactSyncRequestData = new ContactSyncRequestData();
                if (!phoneNumber.getPhoneNumbers().isEmpty()) {
                    for (String phoneNum : phoneNumber.getPhoneNumbers()) {
                        contactSyncRequestData.setOriginalNumber(phoneNum);
                        try {
                            Phonenumber.PhoneNumber cleanedPhoneNumber = phoneUtil.parse(phoneNum, "IN");
                            String number = "";
                            if (cleanedPhoneNumber.hasNationalNumber()) {
                                number = String.valueOf(cleanedPhoneNumber.getNationalNumber());
                            }
                            contactSyncRequestData.setPureNumber(number);
                        } catch (NumberParseException e) {
                            contactSyncRequestData.setPureNumber(phoneNum);
                        }
                        contactSyncRequestDataList.add(contactSyncRequestData);
                    }
                }
            }
        }
        request.setData(contactSyncRequestDataList);
        FRLogger.msg("request object " + gson.toJson(request));
        return gson.toJson(request);
    }

}
