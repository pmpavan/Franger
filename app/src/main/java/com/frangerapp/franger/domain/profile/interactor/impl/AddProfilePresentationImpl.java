package com.frangerapp.franger.domain.profile.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.ProfileApi;
import com.frangerapp.franger.domain.login.exception.LoginFailedException;
import com.frangerapp.franger.domain.login.interactor.impl.LoginPresenterImpl;
import com.frangerapp.franger.domain.profile.exception.ProfileUpdateFailed;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.viewmodel.login.util.LoginValidator;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Pavan on 08/02/18.
 */

public class AddProfilePresentationImpl implements ProfileInteractor {

    private static final String TAG = LoginPresenterImpl.class.getName();

    private Context context;
    private LoginValidator validator;
    private ProfileApi profileApi;
    private UserStore userStore;
    private AppStore appStore;

    public AddProfilePresentationImpl(@NonNull Context context, @NonNull ProfileApi profileApi, UserStore userStore) {
        this.context = context;
        this.profileApi = profileApi;
        this.userStore = userStore;
    }

    @Override
    public Completable submitProfile(@NotNull String userId, @NotNull String userName) {
        return profileApi.editProfile(userId, userName).flatMap(loginDetail -> Single.create(s -> {
            if (loginDetail.getUpdated() != null && loginDetail.getUpdated().contains("name")) {
                userStore.setUserProfileCollected(context, true);
                userStore.storeUserName(context, userName);
                FRLogger.msg("profile updated " + loginDetail);
                s.onSuccess(loginDetail);
            } else {
                s.onError(new ProfileUpdateFailed());
            }
        })).toCompletable();
    }
}
