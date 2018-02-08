package com.frangerapp.franger.domain.splash.interactor.impl;

import android.content.Context;

import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.splash.interactor.SplashInteractor;

/**
 * Created by Pavan on 03/02/18.
 */

public class SplashPresentationImpl implements SplashInteractor {

    private Context context;
    private AppStore appStore;
    private UserStore userStore;

    public SplashPresentationImpl(Context context, AppStore appStore, UserStore userStore) {
        this.context = context;
        this.appStore = appStore;
        this.userStore = userStore;
    }

    @Override
    public void setAppVersionCode(int versionCode) {
        appStore.setAppVersionCode(versionCode);
    }
}
