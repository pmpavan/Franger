package com.frangerapp.franger.viewmodel.invite;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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

//        List<String> list = new ArrayList<>();
//        list.add("+91-9790708464");
//        list.add("+91-9840636541");
//        list.add("+91-9445589121");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9790708464");
//        list.add("+91-9962344255");
//        list.add("+91-9962344255");
//        list.add("+91-9962344255");
//        list.add("+91-9962344255");
//        list.add("+91-9962344255");
//        list.add("+91-9789098830");
        profileInteractor.syncContacts(user.getUserId())
                .compose(SchedulerUtils.ioToMainCompletableScheduler())
                .subscribe(this::onSuccess, this::onFailure);
    }

    private void onFailure(Throwable throwable) {

    }

    private void onSuccess() {

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