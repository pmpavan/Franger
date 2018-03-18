package com.frangerapp.franger.viewmodel.chat;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.franger.socket.SocketHelper;
import com.franger.socket.SocketIOCallbacks;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.chat.eventbus.ChatEvent;
import com.frangerapp.franger.viewmodel.chat.util.ChatPresentationConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

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

    public ObservableField<String> messageTxt = new ObservableField<>("");

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
        sendSetToolbarTitleTxtEvent();
        chatInteractor.addChatEvent(chatContact.getUserId(), isIncoming, this);
    }

    private void sendSetToolbarTitleTxtEvent() {
        ChatEvent event = new ChatEvent();
        event.setId(ChatPresentationConstants.SET_TOOLBAR_TXT);
        event.setMessage(chatContact.getDisplayName());
        eventBus.post(event);
    }

    public void onSendButtonClicked() {
        if (!messageTxt.get().isEmpty()) {
            chatInteractor.sendMessage(chatContact.getUserId(), isIncoming, messageTxt.get(), this);
            messageTxt.set("");
        }
    }

    @Override
    public void onConnecting(String TAG) {
        FRLogger.msg("Chat onConnecting " + TAG);
    }

    @Override
    public void onSocketCreated(String TAG) {
        FRLogger.msg("Chat onSocketCreated " + TAG);
    }

    @Override
    public void onMessage(String TAG, String message) {
        FRLogger.msg("Chat onMessage " + TAG + ' ' + message);
    }

    @Override
    public void progressChanged(String TAG, int progress) {
        FRLogger.msg("Chat progressChanged " + TAG + ' ' + progress);
    }

    @Override
    public void on(String TAG, String event, Object... args) {
        JSONObject data = (JSONObject) args[0];
        FRLogger.msg("Chat on " + TAG + ' ' + event + " " + data);

//        String json = gson.fromJson(data.toString(), FeedNewMessageResponse::class.java)
//        FrLogger.msg("message is $args ${json.channel}")
//
    }

    @Override
    public void onError(String TAG, SocketHelper errorCode) {
        FRLogger.msg("Chat onError " + TAG + ' ' + errorCode.getMessage());

    }

    @Override
    public void onDisconnecting(String TAG) {
        FRLogger.msg("Chat onDisconnecting " + TAG);

    }

    @Override
    public void onSocketDestroyed(String TAG) {
        FRLogger.msg("Chat onSocketDestroyed " + TAG);

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
