package com.frangerapp.franger.viewmodel.countries;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.domain.countries.interactor.CountriesInteractor;
import com.frangerapp.franger.ui.BaseBindingAdapters;
import com.frangerapp.franger.viewmodel.common.databinding.FieldUtils;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.frangerapp.franger.viewmodel.countries.converter.CountriesConverter;
import com.frangerapp.franger.viewmodel.countries.eventbus.CountriesViewEvent;
import com.frangerapp.franger.viewmodel.countries.util.CountriesPresentationConstants;
import com.frangerapp.franger.viewmodel.login.LoginBaseViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Pavan on 22/01/18.
 */

public class CountriesViewModel extends LoginBaseViewModel {

    private ArrayList<CountriesListItemViewModel> list = new ArrayList<>();
    public ObservableField<List<CountriesListItemViewModel>> countriesList = new ObservableField<>(list);

    private List<CountriesListItemViewModel> itemViewModels = new ArrayList<>();
    public ObservableField<String> searchTxt = new ObservableField<>("");

    private CountriesInteractor countriesInteractor;
    private EventBus eventBus;
    private Context context;
    private Gson gson;

    public CountriesViewModel(Context context, EventBus eventBus, CountriesInteractor countriesInteractor, Gson gson) {
        this.context = context;
        this.eventBus = eventBus;
        this.countriesInteractor = countriesInteractor;
        this.gson = gson;
        init();
    }

    private void init() {
        Disposable disposable = countriesInteractor.getCountriesList()
                .toObservable()
                .flatMapIterable(agents -> agents)
                .flatMapSingle(CountriesConverter::convert)
                .toList()
                .compose(SchedulerUtils.ioToMainSingleScheduler())
                .subscribe(this::onSuccess, this::onError);
        disposables.add(disposable);

        FieldUtils.toObservable(searchTxt)
                .debounce(10L, TimeUnit.MILLISECONDS)
                .subscribe(this::filterList, t -> FRLogger.msg("Error " + t.getMessage()), this::onComplete);
    }

    private void onComplete() {

    }

    private void filterList(String text) {
        FRLogger.msg("String " + searchTxt);
        Observable.just(itemViewModels)
                .concatMapIterable(activityTypes -> activityTypes)
                .filter(activityTypes -> text.isEmpty()
                        || (activityTypes.getCountryName().toLowerCase().contains(text.toLowerCase())
                        || activityTypes.getCountryCode().toLowerCase().contains(text.toLowerCase())))
                .toList()
                .compose(SchedulerUtils.ioToMainSingleScheduler())
                .subscribe(this::onSuccess, t -> FRLogger.msg("Error " + t.getMessage()));
    }

    private void onError(Throwable throwable) {
        FRLogger.msg(throwable.getMessage());

    }

    private void onSuccess(List<CountriesListItemViewModel> countriesListItemViewModels) {
        FRLogger.msg(countriesListItemViewModels.toString());
        itemViewModels = countriesListItemViewModels;
        countriesList.set(countriesListItemViewModels);
    }


    public final BaseBindingAdapters.ItemClickHandler<CountriesListItemViewModel> itemClickHandler = (position, item) -> {
        FRLogger.msg("item " + item);
        CountriesViewEvent countriesViewEvent = CountriesViewEvent.builder();
        countriesViewEvent.setId(CountriesPresentationConstants.COUNTRY_ITEM_CLICKED);
        countriesViewEvent.setCountriesListItemViewModel(item);
        eventBus.post(countriesViewEvent);
    };

    public static class Factory implements ViewModelProvider.Factory {

        private CountriesInteractor profileInteractor;
        private EventBus eventBus;
        private Context context;
        private Gson gson;

        public Factory(Context context, EventBus eventBus, CountriesInteractor countriesInteractor, Gson gson) {
            this.context = context;
            this.eventBus = eventBus;
            this.profileInteractor = countriesInteractor;
            this.gson = gson;

        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(CountriesViewModel.class)) {
                return (T) new CountriesViewModel(context, eventBus, profileInteractor, gson);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
