package com.frangerapp.franger.domain.login.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.login.LoginApi;
import com.frangerapp.franger.domain.login.exception.LoginFailedException;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.viewmodel.login.LoginValidator;

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
                //TODO Save User details
                userStore.storeUserId(context, loginDetail.getId());
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
}
