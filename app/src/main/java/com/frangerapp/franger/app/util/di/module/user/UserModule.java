package com.frangerapp.franger.app.util.di.module.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.app.util.di.scope.UserScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.ProfileApi;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.profile.interactor.impl.ProfilePresentationImpl;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.network.HttpClient;
import com.google.gson.Gson;

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
                                        @NonNull UserStore userStore, AppDatabase appDatabase) {
        return new ProfilePresentationImpl(context, loginApi, userStore,appDatabase);
    }
}
