package com.frangerapp.franger.app.util.di.module.login;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.ui.login.LoginActivity;
import com.frangerapp.franger.ui.login.VerifyUserActivity;
import com.frangerapp.franger.viewmodel.login.VerifyUserViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 04/02/18.
 */
@Module
public class VerifyUserModule {

    public VerifyUserModule(@NonNull VerifyUserActivity activity) {
    }

    @Provides
    @ActivityScope
    ViewModelProvider.Factory verifyUserActivityViewModel(@NonNull Context context, @NonNull LoginInteractor loginInteractor, @NotNull EventBus eventBus, UserStore userStore) {
        return new VerifyUserViewModel.Factory(context, eventBus, loginInteractor, userStore);
    }
}
