package com.frangerapp.franger.data.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavan on 12/02/18.
 */

public class ContactSyncRequest {
    @SerializedName("data")
    @Expose
    private List<ContactSyncRequestData> data = new ArrayList<>();

    public ContactSyncRequest() {

    }

    public List<ContactSyncRequestData> getData() {
        return data;
    }

    public void setData(List<ContactSyncRequestData> data) {
        this.data = data;
    }
}
