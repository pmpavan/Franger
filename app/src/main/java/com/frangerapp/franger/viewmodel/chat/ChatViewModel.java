package com.frangerapp.franger.viewmodel.chat;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.socket.SocketHelper;
import com.franger.socket.SocketIOCallbacks;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by pavanm on 13/03/18.
 */

public class ChatViewModel extends UserBaseViewModel implements SocketIOCallbacks {

    private User user;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private ChatContact chatContact;
    private boolean isIncoming;

    public ChatViewModel(Context context, EventBus eventBus, UserStore userStore, User user, ChatInteractor chatInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.user = user;

    }

    public void onPageLoaded(ChatContact chatContact, boolean isIncoming) {
        this.isIncoming = isIncoming;
        this.chatContact = chatContact;
        chatInteractor.addChatEvent(chatContact.getUserId(), isIncoming, this);
        chatInteractor.sendMessage(chatContact.getUserId(), isIncoming,"hard work nevers fails bro", this);
    }

    @Override
    public void onConnecting(String TAG) {

    }

    @Override
    public void onSocketCreated(String TAG) {

    }

    @Override
    public void onMessage(String TAG, String message) {

    }

    @Override
    public void progressChanged(String TAG, int progress) {

    }

    @Override
    public void on(String TAG, String event, Object... args) {

    }

    @Override
    public void onError(String TAG, SocketHelper errorCode) {

    }

    @Override
    public void onDisconnecting(String TAG) {

    }

    @Override
    public void onSocketDestroyed(String TAG) {

    }

    public static class Factory implements ViewModelProvider.Factory {

        private ChatInteractor chatInteractor;
        private EventBus eventBus;
        private User user;
        private Context context;
        private UserStore userStore;

        public Factory(Context context, EventBus eventBus, UserStore userStore, User user, ChatInteractor chatInteractor) {
            this.context = context;
            this.user = user;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.chatInteractor = chatInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ChatViewModel.class)) {
                return (T) new ChatViewModel(context, eventBus, userStore, user, chatInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
