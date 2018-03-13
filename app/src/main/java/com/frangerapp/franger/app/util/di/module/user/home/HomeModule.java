package com.frangerapp.franger.app.util.di.module.user.home;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.ui.home.HomeActivity;
import com.frangerapp.franger.viewmodel.home.HomeViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 24/01/18.
 */

@Module
public class HomeModule {

    public HomeModule(@NonNull HomeActivity activity) {
    }

    @ActivityScope
    @Provides
    ViewModelProvider.Factory homeActivityViewModel(@NonNull Context context, @NonNull User user, @NotNull EventBus eventBus, @NonNull UserStore userStore) {
        return new HomeViewModel.Factory(context, eventBus, userStore, user);
    }


}