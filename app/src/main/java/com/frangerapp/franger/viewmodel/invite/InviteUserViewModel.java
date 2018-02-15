package com.frangerapp.franger.viewmodel.invite;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Pavan on 07/02/18.
 */

public class InviteUserViewModel extends UserBaseViewModel {


    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ProfileInteractor profileInteractor;

    public InviteUserViewModel(Context context, EventBus eventBus, UserStore userStore, User user, ProfileInteractor profileInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.profileInteractor = profileInteractor;
    }


    public static class Factory implements ViewModelProvider.Factory {

        private ProfileInteractor profileInteractor;
        private EventBus eventBus;
        private User user;
        private Context context;
        private UserStore userStore;

        public Factory(Context context, EventBus eventBus, UserStore userStore, User user, ProfileInteractor profileInteractor) {
            this.context = context;
            this.user = user;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.profileInteractor = profileInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(InviteUserViewModel.class)) {
                return (T) new InviteUserViewModel(context, eventBus, userStore, user, profileInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }

}
