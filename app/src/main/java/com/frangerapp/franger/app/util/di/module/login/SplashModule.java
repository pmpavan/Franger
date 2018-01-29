package com.frangerapp.franger.app.util.di.module.login;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.UserManager;
import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.app.util.di.scope.FragmentScope;
import com.frangerapp.franger.ui.splash.SplashActivity;
import com.frangerapp.franger.viewmodel.splash.SplashViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 24/01/18.
 */

@Module
public class SplashModule {

    public SplashModule(@NonNull SplashActivity activity) {
    }

//    @ActivityScope
//    @Provides
//    SplashViewModel splashActivityViewModel(@NonNull Context context, @NonNull UserManager userManager, @NotNull EventBus eventBus) {
//        return new SplashViewModel(context, eventBus, userManager);
//    }

    @Provides
    @ActivityScope
    ViewModelProvider.Factory splashActivityViewModel(@NonNull Context context, @NonNull UserManager userManager, @NotNull EventBus eventBus) {
        return new SplashViewModel.Factory(context, eventBus, userManager);
    }
}
