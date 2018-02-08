package com.frangerapp.franger.app.util.di.module.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.LoginScope;
import com.frangerapp.franger.app.util.di.scope.UserScope;
import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.login.LoginApi;
import com.frangerapp.franger.data.profile.ProfileApi;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.domain.login.interactor.impl.LoginPresenterImpl;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.profile.interactor.impl.AddProfilePresentationImpl;
import com.frangerapp.franger.domain.user.model.User;
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
public class UserModule {
    private User user;

    public UserModule(@NonNull User user) {
        this.user = user;
    }

    /**
     * COMMON MODULE.
     */
    @Provides
    @UserScope
    User user() {
        return user;
    }


    @UserScope
    @Provides
    ProfileApi profileApi(Context context, HttpClient httpClient, Gson gson) {
        return new ProfileApi(context, httpClient, gson);
    }

    @UserScope
    @Provides
    ProfileInteractor profileInteractor(@NonNull Context context, @NonNull ProfileApi loginApi,
                                        @NonNull UserStore userStore) {
        return new AddProfilePresentationImpl(context, loginApi, userStore);
    }
}
