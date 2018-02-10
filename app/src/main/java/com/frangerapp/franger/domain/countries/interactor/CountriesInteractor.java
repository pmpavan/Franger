package com.frangerapp.franger.domain.countries.interactor;

import android.support.annotation.NonNull;

import com.frangerapp.franger.data.countries.model.Country;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Pavan on 07/02/18.
 */

public interface CountriesInteractor {

    @NonNull
    Single<List<Country>> getCountriesList();
}
