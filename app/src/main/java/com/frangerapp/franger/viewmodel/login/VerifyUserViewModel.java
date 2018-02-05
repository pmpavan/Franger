package com.frangerapp.franger.viewmodel.login;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.common.rx.ScheduerUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.Disposable;

/**
 * Created by Pavan on 04/02/18.
 */

public class VerifyUserViewModel extends BaseViewModel {

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
            Disposable disposable = loginInteractor.verifyPhoneNumber(userId, verifyOtpTxt.get())
                    .compose(ScheduerUtils.ioToMainCompletableScheduler())
                    .subscribe(this::requestCompletedEvent, this::requestFailedEvent);
            disposables.add(disposable);
        }
    }

    private void requestFailedEvent(Throwable throwable) {

    }

    private void requestCompletedEvent() {

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
