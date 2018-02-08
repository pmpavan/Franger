package com.frangerapp.franger.domain.login.interactor;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;

/**
 * Created by Pavan on 03/02/18.
 */

public interface LoginInteractor {

    Completable registerUser(@NonNull final String username, @NotNull String countryCode, @NonNull final String phoneNumber);

    boolean validateNumber(@NotNull String countryCode, @NonNull String phoneNumber);

    Completable verifyPhoneNumber(@NonNull final String userId, @NotNull final String userEnteredOtp);

    void setAppVersionCode(int versionCode);
}
