package com.frangerapp.franger.data.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.network.HttpClient;

/**
 * Created by Pavan on 23/01/18.
 */

public class LoginApi {
    private static final String TAG = "LoginApi";

    private HttpClient httpClient;
    private Context context;

    public LoginApi(@NonNull Context context, @NonNull HttpClient httpClient) {
        this.context = context;
        this.httpClient = httpClient;
    }

}
