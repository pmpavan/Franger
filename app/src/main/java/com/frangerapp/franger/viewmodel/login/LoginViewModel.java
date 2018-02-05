package com.frangerapp.franger.viewmodel.login;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.R;
import com.frangerapp.franger.domain.login.interactor.LoginInteractor;
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.common.rx.ScheduerUtils;
import com.frangerapp.franger.viewmodel.countries.CountriesListItemViewModel;
import com.frangerapp.franger.viewmodel.login.eventbus.LoginViewEvent;
import com.frangerapp.franger.viewmodel.login.util.LoginPresentationConstants;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.reactivex.disposables.Disposable;

/**
 * Created by Pavan on 20/01/18.
 */

public class LoginViewModel extends BaseViewModel {


    private LoginInteractor loginInteractor;
    private EventBus eventBus;
    private Context context;
    public ObservableField<String> phoneNumberTxt = new ObservableField<>("");
    public ObservableField<String> countryCodeWithCountryNameTxt = new ObservableField<>("");
    public ObservableField<String> countryCodeTxt = new ObservableField<>("");
    public ObservableBoolean phoneNumberTxtVisibility = new ObservableBoolean(false);

    private String countryCode = "";

    public LoginViewModel(Context context, EventBus eventBus, LoginInteractor loginInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.loginInteractor = loginInteractor;
        init();
    }

    private void init() {
        phoneNumberTxt.set("");
        phoneNumberTxtVisibility.set(false);
    }

    public void onPageLoaded() {

    }

    public void onVerifyClicked() {
        if (loginInteractor.validateNumber(countryCode, phoneNumberTxt.get())) {
            phoneNumberTxtVisibility.set(false);
            requestSentEvent();
            //send register request
            Disposable disposable = loginInteractor.registerUser("", countryCode, phoneNumberTxt.get())
                    .compose(ScheduerUtils.ioToMainCompletableScheduler())
                    .subscribe(this::requestCompletedEvent, this::requestFailedEvent);
            disposables.add(disposable);
        } else {
            phoneNumberTxtVisibility.set(true);
        }
    }

    private void requestSentEvent() {
        LoginViewEvent loginViewEvent = new LoginViewEvent();
        loginViewEvent.setId(LoginPresentationConstants.VALID_NUMBER_REQUEST_SENT);
        eventBus.post(loginViewEvent);
    }

    private void requestCompletedEvent() {
        LoginViewEvent loginViewEvent = new LoginViewEvent();
        loginViewEvent.setId(LoginPresentationConstants.VALID_NUMBER_CHECK_SUCCESS);
        loginViewEvent.setMessage("OTP message sent");
        eventBus.post(loginViewEvent);
    }

    private void requestFailedEvent(Throwable throwable) {
        LoginViewEvent loginViewEvent = new LoginViewEvent();
        loginViewEvent.setId(LoginPresentationConstants.VALID_NUMBER_CHECK_FAIL);
        loginViewEvent.setMessage(throwable.getMessage());
        eventBus.post(loginViewEvent);
    }

    public void onCountryCodeClick() {
        LoginViewEvent loginViewEvent = new LoginViewEvent();
        loginViewEvent.setId(LoginPresentationConstants.ON_COUNTRY_CODE_CLICKED);
        EventBus.getDefault().post(loginViewEvent);
    }

    public void onCountryCodeSelected(@Nullable CountriesListItemViewModel countriesListItemViewModel) {
        if (countriesListItemViewModel != null) {
            countryCodeWithCountryNameTxt.set(countriesListItemViewModel.getCountryName() + " (" + countriesListItemViewModel.getCountryCode() + ")");
            countryCodeTxt.set(countriesListItemViewModel.getCountryCode() + " - ");
            countryCode = countriesListItemViewModel.getCountryCode();
        }
    }


    public static class Factory implements ViewModelProvider.Factory {

        private EventBus eventBus;
        private Context context;
        private LoginInteractor loginInteractor;

        public Factory(Context context, EventBus eventBus, LoginInteractor loginInteractor) {
            this.eventBus = eventBus;
            this.context = context;
            this.loginInteractor = loginInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LoginViewModel.class)) {
                return (T) new LoginViewModel(context, eventBus, loginInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
