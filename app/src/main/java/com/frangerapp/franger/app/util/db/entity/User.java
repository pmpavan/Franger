package com.frangerapp.franger.app.util.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * Created by pavanm on 22/02/18.
 */

@Entity
public class User {
    @PrimaryKey
    @NotNull
    public String phoneNumber;
    @NotNull
    public String cleanedPhoneNumber;
    public String userId = "0";
    public String displayName;
    public String phoneNumberType;

    @Override
    public String toString() {
        return "User{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", cleanedPhoneNumber='" + cleanedPhoneNumber + '\'' +
                ", userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", phoneNumberType='" + phoneNumberType + '\'' +
                '}';
    }
}
