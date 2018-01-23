package com.frangerapp.franger.viewmodel.countries;

import android.databinding.ObservableField;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.ui.BaseBindingAdapters;
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.countries.CountriesListItemViewModel;
import com.frangerapp.franger.viewmodel.countries.eventbus.CountriesViewEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavan on 22/01/18.
 */

public class CountriesViewModel extends BaseViewModel {

    private ArrayList<CountriesListItemViewModel> list = new ArrayList<>();
    public ObservableField<List<CountriesListItemViewModel>> countriesList = new ObservableField<>(list);

    public CountriesViewModel() {
        init();
    }

    private void init() {
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("India", "+91"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        list.add(new CountriesListItemViewModel("US", "+1"));
        countriesList.set(list);
    }


    public final BaseBindingAdapters.ItemClickHandler<CountriesListItemViewModel> itemClickHandler = (position, item) -> {
        FRLogger.msg("item " + item);
        EventBus.getDefault().post(CountriesViewEvent.builder().setCountriesListItemViewModel(item));
    };
}
