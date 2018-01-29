package com.frangerapp.franger.app.util.di.module.user.home;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.ui.home.HomeActivity;
import com.frangerapp.franger.viewmodel.home.HomeViewModel;
import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 24/01/18.
 */

@Module
public class HomeModule {

    public HomeModule(@NonNull HomeActivity activity) {
    }

    @Provides
    Gson newGson(){
        return new Gson();
    }
    @ActivityScope
    @Provides
    HomeViewModel homePresenter(@NonNull Context context, @NonNull User user) {
        return new HomeViewModel(context, user);
    }
}