package com.frangerapp.franger.data.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavan on 09/02/18.
 */

public class ProfileDataRequest {

    @SerializedName("name")
    @Expose
    private String name;

    public ProfileDataRequest() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
