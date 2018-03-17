package com.frangerapp.franger.viewmodel.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.franger.socket.SocketHelper;
import com.franger.socket.SocketIOCallbacks;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.contact.ContactViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by Pavan on 24/01/18.
 */

public class HomeViewModel extends BaseViewModel implements SocketIOCallbacks {

    private EventBus eventBus;
    private User user;
    private Context context;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private Gson gson;

    public HomeViewModel(Context context, User user, EventBus eventBus, UserStore userStore, ChatInteractor chatInteractor, Gson gson) {
        this.context = context;
        this.user = user;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.gson = gson;
        chatInteractor.addFeedEvent(this);
    }

    @Override
    public void onConnecting(String TAG) {

        FRLogger.msg("onConnecting " + TAG);
    }

    @Override
    public void onSocketCreated(String TAG) {
        FRLogger.msg("onSocketCreated " + TAG);

    }

    @Override
    public void onMessage(String TAG, String message) {
        FRLogger.msg("onMessage " + TAG + ' ' + message);

    }

    @Override
    public void progressChanged(String TAG, int progress) {
        FRLogger.msg("progressChanged " + TAG + ' ' + progress);

    }

    @Override
    public void on(String TAG, String event, Object... args) {
        FRLogger.msg("on " + TAG + ' ' + event + " " + args);
        JSONObject data = (JSONObject) args[0];
        FRLogger.msg("feed is  data " + data);

//        String json = gson.fromJson(data.toString(), FeedNewMessageResponse::class.java)
//        FrLogger.msg("message is $args ${json.channel}")
//
    }

    @Override
    public void onError(String TAG, SocketHelper errorCode) {
        FRLogger.msg("onError " + TAG + ' ' + errorCode.getMessage());

    }

    @Override
    public void onDisconnecting(String TAG) {
        FRLogger.msg("onDisconnecting " + TAG);

    }

    @Override
    public void onSocketDestroyed(String TAG) {
        FRLogger.msg("onSocketDestroyed " + TAG);

    }


    public static class Factory implements ViewModelProvider.Factory {

        private EventBus eventBus;
        private User user;
        private Context context;
        private UserStore userStore;
        private ChatInteractor chatInteractor;
        private Gson gson;

        public Factory(Context context, EventBus eventBus, UserStore userStore, User user, ChatInteractor chatInteractor, Gson gson) {
            this.context = context;
            this.user = user;
            this.gson = gson;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.chatInteractor = chatInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(HomeViewModel.class)) {
                return (T) new HomeViewModel(context, user, eventBus, userStore, chatInteractor, gson);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
