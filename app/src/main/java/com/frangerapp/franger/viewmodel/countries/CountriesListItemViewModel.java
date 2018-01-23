package com.frangerapp.franger.viewmodel.countries;

import android.databinding.ObservableField;

import com.frangerapp.franger.viewmodel.BaseViewModel;

/**
 * Created by Pavan on 22/01/18.
 */

public class CountriesListItemViewModel extends BaseViewModel {

    public ObservableField<String> countryNameTxt = new ObservableField<>("");

    private String countryName;
    private String countryCode;


    public CountriesListItemViewModel(String countryName, String countryCode) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        countryNameTxt.set(countryName + " (" + countryCode + ")");
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return "CountriesListItemViewModel{" +
                "countryNameTxt=" + countryNameTxt +
                ", countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
