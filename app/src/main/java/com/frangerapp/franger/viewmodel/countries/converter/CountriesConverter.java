package com.frangerapp.franger.viewmodel.countries.converter;

import android.support.annotation.NonNull;

import com.frangerapp.franger.data.countries.model.Country;
import com.frangerapp.franger.viewmodel.countries.CountriesListItemViewModel;

import io.reactivex.Single;

/**
 * Created by Pavan on 10/02/18.
 */

public class CountriesConverter {
    @NonNull
    public static Single<CountriesListItemViewModel> convert(@NonNull Country agent) {
        return Single.fromCallable(() -> convertToCountriesListViewModelItem(agent));
    }

    @NonNull
    private static CountriesListItemViewModel convertToCountriesListViewModelItem(@NonNull final Country agent) {
        return new CountriesListItemViewModel(agent.getName(), agent.getCode());
    }
}
