package com.frangerapp.franger.data.countries.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavan on 10/02/18.
 */

public class CountriesListWrapper {

    @SerializedName("countries")
    @Expose
    private List<Country> countries = new ArrayList<>();

    public CountriesListWrapper() {

    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }
}
