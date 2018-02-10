package com.frangerapp.franger.viewmodel.profile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.common.rx.ScheduerUtils;
import com.frangerapp.franger.viewmodel.profile.eventbus.ProfileViewEvent;
import com.frangerapp.franger.viewmodel.profile.util.ProfilePresentationConstants;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.Disposable;

/**
 * Created by Pavan on 07/02/18.
 */

public class ProfileViewModel extends BaseViewModel {

    private Context context;
    private User user;
    private UserStore userStore;
    private EventBus eventBus;
    private ProfileInteractor profileInteractor;


    public ObservableField<String> userNameTxt = new ObservableField<>("");
    public ObservableInt errorVisibility = new ObservableInt(View.INVISIBLE);

    public ProfileViewModel(Context context, User user, UserStore userStore, EventBus eventBus, ProfileInteractor profileInteractor) {
        this.context = context;
        this.user = user;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.profileInteractor = profileInteractor;
    }

    public void onPageLoaded() {
        errorVisibility.set(View.INVISIBLE);
    }

    public void onProfileSubmitBtnClicked() {
        if (!userNameTxt.get().isEmpty()) {
            errorVisibility.set(View.INVISIBLE);
            requestSentEvent();
            //send register request
            Disposable disposable = profileInteractor.submitProfile(user.getUserId(), userNameTxt.get())
                    .compose(ScheduerUtils.ioToMainCompletableScheduler())
                    .subscribe(this::onSuccess, this::onFailure);
            disposables.add(disposable);
        } else {
            errorVisibility.set(View.VISIBLE);
        }
    }

    private void onFailure(Throwable throwable) {
        ProfileViewEvent profileViewEvent = new ProfileViewEvent();
        profileViewEvent.setId(ProfilePresentationConstants.ON_PROFILE_REQUEST_FAILURE);
        profileViewEvent.setMessage(throwable.getMessage());
        eventBus.post(profileViewEvent);
    }

    private void onSuccess() {
        ProfileViewEvent profileViewEvent = new ProfileViewEvent();
        profileViewEvent.setId(ProfilePresentationConstants.ON_PROFILE_REQUEST_SUCCESS);
        eventBus.post(profileViewEvent);
    }

    private void requestSentEvent() {
        ProfileViewEvent profileViewEvent = new ProfileViewEvent();
        profileViewEvent.setId(ProfilePresentationConstants.ON_PROFILE_REQUEST_SENT);
        eventBus.post(profileViewEvent);
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
