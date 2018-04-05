package com.frangerapp.franger.app.util.di.module.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.socket.socketio.SocketManager;
import com.frangerapp.franger.app.ChatStore;
import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.app.util.di.scope.UserScope;
import com.frangerapp.franger.data.chat.ChatApi;
import com.frangerapp.franger.domain.chat.util.ChatDataUtil;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.ProfileApi;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.interactor.impl.ChatPresentationImpl;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.profile.interactor.impl.ProfilePresentationImpl;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.network.HttpClient;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 24/01/18.
 */
@Module
public class UserModule {
    private LoggedInUser loggedInUser;

    public UserModule(@NonNull LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    /**
     * COMMON MODULE.
     */
    @Provides
    @UserScope
    LoggedInUser user() {
        return loggedInUser;
    }


    @UserScope
    @Provides
    ProfileApi profileApi(Context context, HttpClient httpClient, Gson gson) {
        return new ProfileApi(context, httpClient, gson);
    }

    @UserScope
    @Provides
    ChatApi chatApi(Context context, SocketManager socketManager, Gson gson, ChatStore chatStore) {
        return new ChatApi(context, gson, socketManager, chatStore);
    }

    @UserScope
    @Provides
    ProfileInteractor profileInteractor(@NonNull Context context, @NonNull ProfileApi loginApi,
                                        @NonNull UserStore userStore, AppDatabase appDatabase) {
        return new ProfilePresentationImpl(context, loginApi, userStore, appDatabase);
    }

    @UserScope
    @Provides
    ChatInteractor chatInteractor(@NonNull Context context, @NonNull ChatApi loginApi, @NotNull LoggedInUser loggedInUser, AppDatabase appDatabase, SocketManager socketManager, Gson gson, Random random) {
        return new ChatPresentationImpl(context, loginApi, loggedInUser, appDatabase, socketManager, gson, random);
    }

    @Provides
    @UserScope
    SocketManager socketIOManager(Context context) {
        return SocketManager.getInstance(context, ChatDataUtil.getDomainName());
    }
}
