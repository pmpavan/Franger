package com.frangerapp.franger.viewmodel.login;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.frangerapp.franger.viewmodel.login.eventbus.VerifyUserViewEvent;
import com.frangerapp.franger.viewmodel.login.util.LoginPresentationConstants;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.Disposable;

/**
 * Created by Pavan on 04/02/18.
 */

public class VerifyUserViewModel extends LoginBaseViewModel {

    private Context context;
    private EventBus eventBus;
    private LoginInteractor loginInteractor;
    private UserStore userManager;
    private String userId;

    public ObservableField<String> verifyOtpTxt = new ObservableField<>("");

    public VerifyUserViewModel(Context context, EventBus eventBus, LoginInteractor loginInteractor, UserStore userManager) {
        this.context = context;
        this.loginInteractor = loginInteractor;
        this.userManager = userManager;
        this.eventBus = eventBus;
    }

    public void onPageLoaded() {
        this.userId = userManager.getUserId(context, "");
    }

    public void onVerifyUserBtnClicked() {
        if (!userId.isEmpty()) {
            requestSentEvent();
            Disposable disposable = loginInteractor.verifyPhoneNumber(userId, verifyOtpTxt.get())
                    .compose(SchedulerUtils.ioToMainCompletableScheduler())
                    .subscribe(this::requestCompletedEvent, this::requestFailedEvent);
            disposables.add(disposable);
        }
    }


    private void requestSentEvent() {
        VerifyUserViewEvent loginViewEvent = new VerifyUserViewEvent();
        loginViewEvent.setId(LoginPresentationConstants.VALID_OTP_REQUEST_SENT);
        eventBus.post(loginViewEvent);
    }

    private void requestCompletedEvent() {
        VerifyUserViewEvent loginViewEvent = new VerifyUserViewEvent();
        loginViewEvent.setId(LoginPresentationConstants.VALID_OTP_REQUEST_SUCCESS);
        loginViewEvent.setMessage("Login successful");
        eventBus.post(loginViewEvent);
    }

    private void requestFailedEvent(Throwable throwable) {
        VerifyUserViewEvent loginViewEvent = new VerifyUserViewEvent();
        loginViewEvent.setId(LoginPresentationConstants.VALID_OTP_REQUEST_FAIL);
        loginViewEvent.setMessage(throwable.getMessage());
        eventBus.post(loginViewEvent);
    }

    public static class Factory implements ViewModelProvider.Factory {

        private UserStore userStore;
        private EventBus eventBus;
        private Context context;
        private LoginInteractor loginInteractor;

        public Factory(Context context, EventBus eventBus, LoginInteractor loginInteractor, UserStore userStore) {
            this.eventBus = eventBus;
            this.context = context;
            this.loginInteractor = loginInteractor;
            this.userStore = userStore;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(VerifyUserViewModel.class)) {
                return (T) new VerifyUserViewModel(context, eventBus, loginInteractor, userStore);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
