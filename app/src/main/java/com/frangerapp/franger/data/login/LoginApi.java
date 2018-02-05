package com.frangerapp.franger.data.login;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.BaseApi;
import com.frangerapp.franger.data.common.util.DataUtil;
import com.frangerapp.franger.data.login.model.LoginRequestData;
import com.frangerapp.franger.data.login.model.LoginResponse;
import com.frangerapp.franger.data.login.util.LoginDataConstants;
import com.frangerapp.franger.data.login.util.LoginDataUtil;
import com.frangerapp.network.HttpClient;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import io.reactivex.Single;

/**
 * Created by Pavan on 23/01/18.
 */

public class LoginApi extends BaseApi {
    private static final String TAG = "LoginApi";

    private HttpClient httpClient;
    private Context context;
    private Gson gson;

    public LoginApi(@NonNull Context context, @NonNull HttpClient httpClient, Gson gson) {
        this.context = context;
        this.httpClient = httpClient;
        this.gson = gson;
    }

    @NonNull
    public Single<LoginResponse> registerUser(@NonNull final String username, @NotNull String countryCode, @NonNull final String phoneNumber) {
        return Single.fromCallable(() -> {
            Map<String, String> headers = DataUtil.getCommonApiHeaders();
            String url = getURL(LoginDataUtil.getLoginUrl());
            String params = LoginDataUtil.getLoginRequestObject(gson, username, countryCode, phoneNumber);
            LoginResponse loginDetail = httpClient.postRequestSynchronously(TAG, headers, url,
                    params, HttpClient.BODY_CONTENT_TYPE_JSON, false, LoginResponse.class);
            return loginDetail;
        });
    }

    @NonNull
    public Single<LoginResponse> verifyUser(@NonNull final String userId, @NotNull final String userEnteredOtp) {
        return Single.fromCallable(() -> {
            Map<String, String> headers = DataUtil.getCommonApiHeaders();
            String url = getURL(LoginDataUtil.getVerifyUrl(userId));
            String params = LoginDataUtil.getLoginVerifyRequestData(gson, userEnteredOtp);
            LoginResponse loginDetail = httpClient.postRequestSynchronously(TAG, headers, url,
                    params, HttpClient.BODY_CONTENT_TYPE_JSON, false, LoginResponse.class);
            return loginDetail;
        });
    }
}
