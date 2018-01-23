package com.frangerapp.franger.viewmodel.login;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.countries.CountriesListItemViewModel;
import com.frangerapp.franger.viewmodel.login.eventbus.LoginViewEvent;
import com.frangerapp.franger.viewmodel.login.util.LoginPresentationConstants;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Pavan on 20/01/18.
 */

public class LoginViewModel extends BaseViewModel {


    public ObservableField<String> phoneNumberTxt = new ObservableField<>("");
    public ObservableField<String> countryCodeWithCountryNameTxt = new ObservableField<>("");
    public ObservableField<String> countryCodeTxt = new ObservableField<>("");
    public ObservableBoolean phoneNumberTxtVisibility = new ObservableBoolean(false);

    public LoginViewModel() {
        init();
    }

    private void init() {
        phoneNumberTxt.set("");
        phoneNumberTxtVisibility.set(false);
    }

    public void onPageLoaded() {

    }

    public void onVerifyClicked() {

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
        }
    }
}
