package com.frangerapp.franger.data.profile;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.data.BaseApi;
import com.frangerapp.franger.data.common.util.DataUtil;
import com.frangerapp.franger.data.login.model.LoginResponse;
import com.frangerapp.franger.data.login.util.LoginDataUtil;
import com.frangerapp.franger.data.profile.model.ProfileResponse;
import com.frangerapp.franger.data.profile.util.ProfileDataConstants;
import com.frangerapp.franger.data.profile.util.ProfileDataUtil;
import com.frangerapp.network.HttpClient;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import io.reactivex.Single;

/**
 * Created by Pavan on 08/02/18.
 */

public class ProfileApi extends BaseApi {

    private static final String TAG = "ProfileApi";

    private HttpClient httpClient;
    private Context context;
    private Gson gson;

    public ProfileApi(@NonNull Context context, @NonNull HttpClient httpClient, Gson gson) {
        this.context = context;
        this.httpClient = httpClient;
        this.gson = gson;
    }


    @NonNull
    public Single<ProfileResponse> editProfile(@NonNull final String userId, @NotNull final String username) {
        return Single.fromCallable(() -> {
            Map<String, String> headers = DataUtil.getCommonApiHeaders();
            String url = getURL(ProfileDataUtil.getEditProfileUrl(userId));
            ProfileResponse response = new ProfileResponse();
            response.setId(userId);
            return response;
        });
    }
}
