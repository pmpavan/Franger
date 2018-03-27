package com.frangerapp.franger.domain.user.model;

/**
 * Created by Pavan on 24/01/18.
 */

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

/**
 * Fixed user properties. To be shared throughout the user component.
 */
public class LoggedInUser {
    private final String phoneNumber;
    private final String countryCode;
    private final String userName;
    private final String userId;
    private final String authToken;


    public LoggedInUser(@NotNull String userId, @NotNull String userName, @NotNull String countryCode, @NonNull String phoneNumber, @NonNull String authToken) {
        this.userId = userId;
        this.userName = userName;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.authToken = authToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getCountryCode() {
        return countryCode;
    }
}