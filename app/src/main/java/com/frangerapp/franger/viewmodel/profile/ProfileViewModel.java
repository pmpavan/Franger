package com.frangerapp.franger.viewmodel.profile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.BaseViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Pavan on 07/02/18.
 */

public class ProfileViewModel extends BaseViewModel {

    private Context context;
    private User user;
    private UserStore userStore;
    private EventBus eventBus;
    private ProfileInteractor profileInteractor;

    public ProfileViewModel(Context context, User user, UserStore userStore, EventBus eventBus, ProfileInteractor profileInteractor) {
        this.context = context;
        this.user = user;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.profileInteractor = profileInteractor;
    }

    public void onPageLoaded() {

    }

    public static class Factory implements ViewModelProvider.Factory {

        private ProfileInteractor profileInteractor;
        private EventBus eventBus;
        private User user;
        private Context context;
        private UserStore userStore;

        public Factory(Context context, User user, EventBus eventBus, UserStore userStore, ProfileInteractor profileInteractor) {
            this.context = context;
            this.user = user;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.profileInteractor = profileInteractor;

        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
                return (T) new ProfileViewModel(context, user, userStore, eventBus, profileInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
