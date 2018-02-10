package com.frangerapp.franger.app.util.di.module.login;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.FragmentScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.countries.interactor.CountriesInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.ui.countries.CountriesListDialogFragment;
import com.frangerapp.franger.viewmodel.countries.CountriesViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 10/02/18.
 */
@Module
public class CountriesListModule {

    public CountriesListModule(@NonNull CountriesListDialogFragment activity) {
    }

    @Provides
    @FragmentScope
    ViewModelProvider.Factory countriesListViewModel(@NonNull Context context, @NotNull EventBus eventBus, @NotNull CountriesInteractor countriesInteractor, @NotNull Gson gson) {
        return new CountriesViewModel.Factory(context, eventBus, countriesInteractor, gson);
    }
}
