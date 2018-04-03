package com.frangerapp.franger.app.util.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by pavanm on 22/02/18.
 */

@Entity(indices = {@Index("userId")})
public class User implements Parcelable {
    @PrimaryKey
    @NotNull
    public String phoneNumber = "";
    @NotNull
    public String cleanedPhoneNumber = "";
    public String userId = "0";
    public String displayName;
    public String phoneNumberType;

    @Override
    public String toString() {
        return "LoggedInUser{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", cleanedPhoneNumber='" + cleanedPhoneNumber + '\'' +
                ", userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", phoneNumberType='" + phoneNumberType + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phoneNumber);
        dest.writeString(this.cleanedPhoneNumber);
        dest.writeString(this.userId);
        dest.writeString(this.displayName);
        dest.writeString(this.phoneNumberType);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.phoneNumber = in.readString();
        this.cleanedPhoneNumber = in.readString();
        this.userId = in.readString();
        this.displayName = in.readString();
        this.phoneNumberType = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
