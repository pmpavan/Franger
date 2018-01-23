package com.frangerapp.franger.viewmodel.countries.eventbus;

import com.frangerapp.franger.viewmodel.BaseEvent;
import com.frangerapp.franger.viewmodel.countries.CountriesListItemViewModel;

/**
 * Created by Pavan on 23/01/18.
 */

public class CountriesViewEvent extends BaseEvent {

    private CountriesListItemViewModel countriesListItemViewModel;

    public static CountriesViewEvent builder() {
        return new CountriesViewEvent();
    }

    public CountriesListItemViewModel getCountriesListItemViewModel() {
        return countriesListItemViewModel;
    }

    public CountriesViewEvent setCountriesListItemViewModel(CountriesListItemViewModel countriesListItemViewModel) {
        this.countriesListItemViewModel = countriesListItemViewModel;
        return this;
    }
}
