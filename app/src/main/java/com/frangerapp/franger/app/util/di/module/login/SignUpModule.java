package com.frangerapp.franger.app.util.di.module.login;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.app.util.di.scope.LoginScope;
import com.frangerapp.franger.data.login.LoginApi;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.ui.login.LoginActivity;
import com.frangerapp.franger.viewmodel.login.LoginViewModel;
import com.frangerapp.network.HttpClient;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 03/02/18.
 */
@Module
public class SignUpModule {

    public SignUpModule(@NonNull LoginActivity activity) {
    }

//    @LoginScope
//    @Provides
//    LoginApi loginApi(Context context, HttpClient httpClient, Gson gson) {
//        return new LoginApi(context, httpClient, gson);
//    }

    @Provides
    @ActivityScope
    ViewModelProvider.Factory signUpActivityViewModel(@NonNull Context context, @NonNull LoginInteractor loginInteractor, @NotNull EventBus eventBus) {
        return new LoginViewModel.Factory(context, eventBus, loginInteractor);
    }
}
