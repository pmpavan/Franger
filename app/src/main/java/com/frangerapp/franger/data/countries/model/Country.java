package com.frangerapp.franger.data.countries.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavan on 10/02/18.
 */

public class Country {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("code")
    @Expose
    private String code;

    public Country() {

    }

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
