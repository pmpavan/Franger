package com.frangerapp.franger.app.util.di.module.user.invite;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.invite.InviteActivity;
import com.frangerapp.franger.viewmodel.invite.InviteUserViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pavanm on 14/02/18.
 */
@Module
public class InviteModule {

    private final Activity activity;

    public InviteModule(@NonNull InviteActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    ViewModelProvider.Factory inviteActivityViewModel(@NonNull Context context, @NonNull LoggedInUser loggedInUser, @NonNull UserStore userStore, @NotNull EventBus eventBus, @NotNull ProfileInteractor profileInteractor) {
        return new InviteUserViewModel.Factory(context, eventBus, userStore, loggedInUser, profileInteractor);
    }
}
