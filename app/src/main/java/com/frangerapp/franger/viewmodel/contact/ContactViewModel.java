package com.frangerapp.franger.viewmodel.contact;

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
 * Created by pavanm on 10/03/18.
 */

public class ContactViewModel extends BaseViewModel {


    private User user;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ProfileInteractor profileInteractor;

    public ContactViewModel(Context context, EventBus eventBus, UserStore userStore, User user, ProfileInteractor profileInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.profileInteractor = profileInteractor;
        this.user = user;
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
            if (modelClass.isAssignableFrom(ContactViewModel.class)) {
                return (T) new ContactViewModel(context, eventBus, userStore, user, profileInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
