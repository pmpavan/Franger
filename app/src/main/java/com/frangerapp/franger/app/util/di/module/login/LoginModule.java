package com.frangerapp.franger.app.util.di.module.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.LoginScope;
import com.frangerapp.franger.app.util.di.scope.UserScope;
import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.countries.CountriesApi;
import com.frangerapp.franger.data.login.LoginApi;
import com.frangerapp.franger.data.profile.ProfileApi;
import com.frangerapp.franger.domain.countries.interactor.CountriesInteractor;
import com.frangerapp.franger.domain.countries.interactor.impl.CountriesPresenterImpl;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.domain.login.interactor.impl.LoginPresenterImpl;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.profile.interactor.impl.AddProfilePresentationImpl;
import com.frangerapp.franger.viewmodel.login.util.LoginValidator;
import com.frangerapp.network.HttpClient;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

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
    LoginApi loginApi(Context context, HttpClient httpClient, Gson gson) {
        return new LoginApi(context, httpClient, gson);
    }

    @LoginScope
    @Provides
    LoginInteractor loginInteractor(@NonNull Context context, @NonNull LoginApi loginApi,
                                    @NonNull UserStore userStore,
                                    @NonNull AppStore appStore,
                                    @NotNull LoginValidator loginValidator) {
        return new LoginPresenterImpl(context, loginValidator, loginApi, userStore, appStore);
    }

    @LoginScope
    @Provides
    CountriesApi countriesApi(Context context, HttpClient httpClient, Gson gson) {
        return new CountriesApi(context, httpClient, gson);
    }

    @LoginScope
    @Provides
    CountriesInteractor countriesInteractor(@NonNull Context context, @NonNull CountriesApi loginApi) {
        return new CountriesPresenterImpl(context, loginApi);
    }
}
