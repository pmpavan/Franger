package com.frangerapp.franger.app.util.di.module.login;

import android.content.Context;

import com.frangerapp.franger.app.util.di.scope.LoginScope;
import com.frangerapp.franger.data.login.LoginApi;
import com.frangerapp.franger.viewmodel.login.LoginValidator;
import com.frangerapp.network.HttpClient;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 24/01/18.
 */
@Module
public class LoginModule {

    @LoginScope
    @Provides
    LoginValidator validator() {
        return new LoginValidator();
    }

    @LoginScope
    @Provides
    LoginApi loginApi(Context context, HttpClient httpClient) {
        return new LoginApi(context, httpClient);
    }
}
