package com.frangerapp.franger.app.util.di.module.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.socket.socketio.SocketIOManager;
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
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.network.HttpClient;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 24/01/18.
 */
@Module
public class UserModule {
    private User user;

    public UserModule(@NonNull User user) {
        this.user = user;
    }

    /**
     * COMMON MODULE.
     */
    @Provides
    @UserScope
    User user() {
        return user;
    }


    @UserScope
    @Provides
    ProfileApi profileApi(Context context, HttpClient httpClient, Gson gson) {
        return new ProfileApi(context, httpClient, gson);
    }

    @UserScope
    @Provides
    ChatApi chatApi(Context context, SocketIOManager socketIOManager, Gson gson) {
        return new ChatApi(context, gson, socketIOManager);
    }

    @UserScope
    @Provides
    ProfileInteractor profileInteractor(@NonNull Context context, @NonNull ProfileApi loginApi,
                                        @NonNull UserStore userStore, AppDatabase appDatabase) {
        return new ProfilePresentationImpl(context, loginApi, userStore, appDatabase);
    }

    @UserScope
    @Provides
    ChatInteractor chatInteractor(@NonNull Context context, @NonNull ChatApi loginApi, @NotNull User user, AppDatabase appDatabase, SocketIOManager socketIOManager, Gson gson) {
        return new ChatPresentationImpl(context, loginApi, user, appDatabase, socketIOManager, gson);
    }

    @Provides
    @UserScope
    SocketIOManager socketIOManager(Context app) {
        return SocketIOManager.getInstance(app, ChatDataUtil.getDomainName());
    }
}
