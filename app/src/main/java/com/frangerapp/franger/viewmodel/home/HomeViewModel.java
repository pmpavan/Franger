package com.frangerapp.franger.viewmodel.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.contact.ContactViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Pavan on 24/01/18.
 */

public class HomeViewModel extends BaseViewModel {

    private EventBus eventBus;
    private User user;
    private Context context;
    private UserStore userStore;
    public HomeViewModel(Context context, User user, EventBus eventBus,UserStore userStore) {
        this.context = context;
        this.user = user;
        this.eventBus = eventBus;
        this.userStore = userStore;
    }


    public static class Factory implements ViewModelProvider.Factory {

        private EventBus eventBus;
        private User user;
        private Context context;
        private UserStore userStore;

        public Factory(Context context, EventBus eventBus, UserStore userStore, User user) {
            this.context = context;
            this.user = user;
            this.eventBus = eventBus;
            this.userStore = userStore;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(HomeViewModel.class)) {
                return (T) new HomeViewModel(context, user, eventBus, userStore);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
