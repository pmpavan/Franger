package com.frangerapp.franger.app.util.di.module.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.UserScope;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.network.HttpClient;

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
}
