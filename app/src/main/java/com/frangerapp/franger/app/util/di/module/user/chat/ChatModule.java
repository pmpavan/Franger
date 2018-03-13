package com.frangerapp.franger.app.util.di.module.user.chat;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.ui.chat.ChatActivity;
import com.frangerapp.franger.viewmodel.chat.ChatViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pavanm on 13/03/18.
 */
@Module
public class ChatModule {

    private final Activity activity;

    public ChatModule(@NonNull ChatActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    ViewModelProvider.Factory chatActivityViewModel(@NonNull Context context, @NonNull User user, @NonNull UserStore userStore, @NotNull EventBus eventBus, @NotNull ProfileInteractor profileInteractor) {
        return new ChatViewModel.Factory(context, eventBus, userStore, user, profileInteractor);
    }
}
