package com.frangerapp.franger.data.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavan on 12/02/18.
 */

public class ContactSyncRequestData {

    @SerializedName("pureNumber")
    @Expose
    private String pureNumber;
    @SerializedName("originalNumber")
    @Expose
    private String originalNumber;


    public ContactSyncRequestData() {
    }

    public String getPureNumber() {
        return pureNumber;
    }

    public void setPureNumber(String pureNumber) {
        this.pureNumber = pureNumber;
    }

    public String getOriginalNumber() {
        return originalNumber;
    }

    public void setOriginalNumber(String originalNumber) {
        this.originalNumber = originalNumber;
    }

}
