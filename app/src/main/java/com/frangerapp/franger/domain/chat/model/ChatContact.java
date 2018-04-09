package com.frangerapp.franger.domain.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.viewmodel.contact.ContactListItemViewModel;

/**
 * Created by pavanm on 16/03/18.
 */

public class ChatContact implements Parcelable {

    private String userId;
    private String anonymisedUserName;
    private int anonymisedUserImg;
    private String phoneNumber;
    private String cleanedPhoneNumber;
    private String displayName;
    private String phoneNumberType;

    public ChatContact() {
    }

    public ChatContact(ContactListItemViewModel user) {
        this.userId = user.userId;
        this.displayName = user.displayName;
        this.phoneNumber = user.phoneNumber;
    }

    public ChatContact(User user) {
        if (user != null) {
            setCleanedPhoneNumber(user.cleanedPhoneNumber);
            setDisplayName(user.displayName);
            setPhoneNumber(user.phoneNumber);
            setUserId(user.userId);
        }
    }

    public ChatContact(String userId, String phoneNumber, String cleanedPhoneNumber, String displayName, String phoneNumberType) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.cleanedPhoneNumber = cleanedPhoneNumber;
        this.displayName = displayName;
        this.phoneNumberType = phoneNumberType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCleanedPhoneNumber() {
        return cleanedPhoneNumber;
    }

    public void setCleanedPhoneNumber(String cleanedPhoneNumber) {
        this.cleanedPhoneNumber = cleanedPhoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumberType() {
        return phoneNumberType;
    }

    public void setPhoneNumberType(String phoneNumberType) {
        this.phoneNumberType = phoneNumberType;
    }

    public String getAnonymisedUserName() {
        return anonymisedUserName;
    }

    public void setAnonymisedUserName(String anonymisedUserName) {
        this.anonymisedUserName = anonymisedUserName;
    }

    public int getAnonymisedUserImg() {
        return anonymisedUserImg;
    }

    public void setAnonymisedUserImg(int anonymisedUserImg) {
        this.anonymisedUserImg = anonymisedUserImg;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.anonymisedUserName);
        dest.writeInt(this.anonymisedUserImg);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.cleanedPhoneNumber);
        dest.writeString(this.displayName);
        dest.writeString(this.phoneNumberType);
    }

    protected ChatContact(Parcel in) {
        this.userId = in.readString();
        this.anonymisedUserName = in.readString();
        this.anonymisedUserImg = in.readInt();
        this.phoneNumber = in.readString();
        this.cleanedPhoneNumber = in.readString();
        this.displayName = in.readString();
        this.phoneNumberType = in.readString();
    }

    public static final Creator<ChatContact> CREATOR = new Creator<ChatContact>() {
        @Override
        public ChatContact createFromParcel(Parcel source) {
            return new ChatContact(source);
        }

        @Override
        public ChatContact[] newArray(int size) {
            return new ChatContact[size];
        }
    };
}
