package com.frangerapp.franger.domain.countries.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.countries.CountriesApi;
import com.frangerapp.franger.data.countries.model.Country;
import com.frangerapp.franger.domain.countries.interactor.CountriesInteractor;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Pavan on 07/02/18.
 */

public class CountriesPresenterImpl implements CountriesInteractor {

    private static final String TAG = CountriesPresenterImpl.class.getName();

    private Context context;
    private CountriesApi profileApi;

    public CountriesPresenterImpl(@NonNull Context context, @NonNull CountriesApi profileApi) {
        this.context = context;
        this.profileApi = profileApi;
    }

    @NonNull
    @Override
    public Single<List<Country>> getCountriesList() {
        return profileApi.getCountriesList().flatMap(loginDetail -> Single.create(s -> {
            if (loginDetail != null) {
                s.onSuccess(loginDetail);
            } else {
                s.onError(new Throwable());
            }
        }));
    }
}
