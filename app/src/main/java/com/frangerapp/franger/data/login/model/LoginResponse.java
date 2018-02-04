package com.frangerapp.franger.data.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavan on 03/02/18.
 */

public class LoginResponse {

    @SerializedName("id")
    @Expose
    private String id;

    public LoginResponse() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "id='" + id + '\'' +
                '}';
    }
}
