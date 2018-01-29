package com.frangerapp.franger.viewmodel.login;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.countries.CountriesListItemViewModel;
import com.frangerapp.franger.viewmodel.login.eventbus.LoginViewEvent;
import com.frangerapp.franger.viewmodel.login.util.LoginPresentationConstants;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

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

    private String countryCode = "";

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
        if (validateNumber()) {
            phoneNumberTxtVisibility.set(false);
            LoginViewEvent loginViewEvent = new LoginViewEvent();
            loginViewEvent.setId(LoginPresentationConstants.VALID_NUMBER_REQUEST_SENT);
            EventBus.getDefault().post(loginViewEvent);
            //send register request
        } else {
            phoneNumberTxtVisibility.set(true);
        }
    }

    private boolean validateNumber() {
        boolean isValid = false;
        String number = countryCode + phoneNumberTxt.get();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(number, "IN");
            isValid = phoneUtil.isValidNumber(swissNumberProto);
        } catch (NumberParseException ignored) {
        }
        return isValid;
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
}
