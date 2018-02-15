package com.frangerapp.franger.data.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavan on 12/02/18.
 */

public class Joined {

    @SerializedName("originalNumber")
    @Expose
    private String originalNumber;
    @SerializedName("userId")
    @Expose
    private String userId;

    public Joined() {

    }

    public String getOriginalNumber() {
        return originalNumber;
    }

    public void setOriginalNumber(String originalNumber) {
        this.originalNumber = originalNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Joined{" +
                "originalNumber='" + originalNumber + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
