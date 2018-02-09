package com.frangerapp.franger.data.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavan on 08/02/18.
 */

public class ProfileResponse {
    @SerializedName("updated")
    @Expose
    private List<String> updated = new ArrayList<>();

    public ProfileResponse() {

    }

    public List<String> getUpdated() {
        return updated;
    }

    public void setUpdated(List<String> updated) {
        this.updated = updated;
    }
}
