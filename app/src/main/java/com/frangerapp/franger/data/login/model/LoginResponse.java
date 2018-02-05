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
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("token")
    @Expose
    private String token;

    public LoginResponse() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "id='" + id + '\'' +
                ", error='" + error + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
