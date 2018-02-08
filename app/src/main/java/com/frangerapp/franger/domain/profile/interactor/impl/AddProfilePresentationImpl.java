package com.frangerapp.franger.domain.profile.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.ProfileApi;
import com.frangerapp.franger.domain.login.interactor.impl.LoginPresenterImpl;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.viewmodel.login.util.LoginValidator;

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
    public void submitProfile() {

    }
}
