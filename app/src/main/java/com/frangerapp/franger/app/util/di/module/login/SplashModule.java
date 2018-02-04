package com.frangerapp.franger.app.util.di.module.login;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.UserManager;
import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.app.util.di.scope.FragmentScope;
import com.frangerapp.franger.app.util.di.scope.LoginScope;
import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.splash.interactor.SplashInteractor;
import com.frangerapp.franger.domain.splash.interactor.impl.SplashPresentationImpl;
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

    @ActivityScope
    @Provides
    SplashInteractor splashInteractor(@NonNull Context context,
                                      @NonNull UserStore userStore,
                                      @NonNull AppStore appStore) {
        return new SplashPresentationImpl(context, appStore, userStore);
    }

    @Provides
    @ActivityScope
    ViewModelProvider.Factory splashActivityViewModel(@NonNull Context context, @NonNull UserManager userManager, @NotNull EventBus eventBus, SplashInteractor splashInteractor) {
        return new SplashViewModel.Factory(context, eventBus, userManager, splashInteractor);
    }
}
