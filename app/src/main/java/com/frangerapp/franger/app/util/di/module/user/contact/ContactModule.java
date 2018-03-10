package com.frangerapp.franger.app.util.di.module.user.contact;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.ui.contact.ContactActivity;
import com.frangerapp.franger.viewmodel.contact.ContactViewModel;
import com.frangerapp.franger.viewmodel.invite.InviteUserViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pavanm on 10/03/18.
 */
@Module
public class ContactModule {
    private final Activity activity;

    public ContactModule(@NonNull ContactActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    ViewModelProvider.Factory contactActivityViewModel(@NonNull Context context, @NonNull User user, @NonNull UserStore userStore, @NotNull EventBus eventBus, @NotNull ProfileInteractor profileInteractor) {
        return new ContactViewModel.Factory(context, eventBus, userStore, user, profileInteractor);
    }
}
