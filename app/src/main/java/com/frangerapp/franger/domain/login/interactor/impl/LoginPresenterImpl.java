package com.frangerapp.franger.domain.login.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.login.LoginApi;
import com.frangerapp.franger.domain.login.exception.LoginFailedException;
import com.frangerapp.franger.domain.login.exception.VerificationFailedException;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.viewmodel.login.util.LoginValidator;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Pavan on 03/02/18.
 */

public class LoginPresenterImpl implements LoginInteractor {

    private static final String TAG = LoginPresenterImpl.class.getName();

    private Context context;
    private LoginValidator validator;
    private LoginApi loginApi;
    private UserStore userStore;
    private AppStore appStore;

    public LoginPresenterImpl(@NonNull Context context, @NonNull LoginValidator loginValidator, LoginApi loginApi, UserStore userStore, AppStore appStore) {
        this.context = context;
        this.validator = loginValidator;
        this.loginApi = loginApi;
        this.userStore = userStore;
        this.appStore = appStore;
    }

    @Override
    public Completable registerUser(@NonNull String username, @NotNull String countryCode, @NonNull String phoneNumber) {
        return loginApi.registerUser(username, countryCode, phoneNumber).flatMap(loginDetail -> Single.create(s -> {
            if (loginDetail.getId() != null) {
                userStore.storeUserId(context, loginDetail.getId());
                FRLogger.msg("user Id " + loginDetail.getId());
                s.onSuccess(loginDetail);
            } else {
                s.onError(new LoginFailedException());
            }
        })).toCompletable();
    }

    @Override
    public boolean validateNumber(@NotNull String countryCode, @NonNull String phoneNumber) {
        FRLogger.msg("validateNumber " + validator.validateNumber(countryCode, phoneNumber));
        return validator.validateNumber(countryCode, phoneNumber);
    }

    @Override
    public Completable verifyPhoneNumber(@NonNull String userId, @NotNull String userEnteredOtp) {
        return loginApi.verifyUser(userId, userEnteredOtp).flatMap(loginDetail -> Single.create(s -> {
            if (loginDetail != null) {
//                userStore.storeUserId(context, loginDetail.getId());
                userStore.setUserVerified(context, true);
                userStore.storeAuthToken(context, loginDetail.getToken());
                s.onSuccess(loginDetail);
            } else {
                s.onError(new VerificationFailedException());
            }
        })).toCompletable();
    }

    @Override
    public void setAppVersionCode(int versionCode) {
        appStore.setAppVersionCode(versionCode);
    }
}
