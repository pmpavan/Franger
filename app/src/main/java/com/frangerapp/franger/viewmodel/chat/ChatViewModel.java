package com.frangerapp.franger.viewmodel.chat;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.viewmodel.chat.eventbus.ChatEvent;
import com.frangerapp.franger.viewmodel.chat.util.ChatPresentationConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by pavanm on 13/03/18.
 */

public class ChatViewModel extends UserBaseViewModel {

    private LoggedInUser loggedInUser;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private ChatContact chatContact;
    private boolean isIncoming;
    private String channelName;
    private Gson gson;

    public ObservableField<String> messageTxt = new ObservableField<>("");

    public ChatViewModel(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor, Gson gson) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.loggedInUser = loggedInUser;
        this.gson = gson;
    }

    public void onPageLoaded(ChatContact chatContact, boolean isIncoming, String channelName) {
        this.isIncoming = isIncoming;
        this.chatContact = chatContact;
        this.channelName = channelName;
        if (channelName == null || channelName.isEmpty()) {
            this.channelName = chatInteractor.getChatEventName(chatContact.getUserId(), isIncoming);
        }
        sendSetToolbarTitleTxtEvent();
        chatInteractor.getMessageEvent()
                .subscribe(getChatMsgObserver());
    }

    private void sendSetToolbarTitleTxtEvent() {
        ChatEvent event = new ChatEvent();
        event.setId(ChatPresentationConstants.SET_TOOLBAR_TXT);
        event.setMessage(chatContact.getDisplayName());
        eventBus.post(event);
    }

    private Observer<MessageEvent> getChatMsgObserver() {
        return new Observer<MessageEvent>() {

            @Override
            public void onSubscribe(Disposable d) {
                FRLogger.msg("chat First onSubscribe : " + d.isDisposed());
            }

            @Override
            public void onNext(MessageEvent messageEvent) {
                FRLogger.msg("chat First onNext value : " + messageEvent);
                handleChatMessages(messageEvent);
            }

            @Override
            public void onError(Throwable e) {
                FRLogger.msg("chat First onError : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                FRLogger.msg("chat First onComplete");
            }
        };
    }

    private void handleChatMessages(MessageEvent messageEvent) {
        if (messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id
                && messageEvent.getChannel().equalsIgnoreCase(channelName)) {
            // update in list
        }
    }

    public void onSendButtonClicked() {
        if (!messageTxt.get().isEmpty()) {
            chatInteractor.addChatEvent(chatContact.getUserId(), isIncoming);
            chatInteractor.sendMessage(chatContact.getUserId(), isIncoming, messageTxt.get());
            messageTxt.set("");
        }
    }

    public static class Factory implements ViewModelProvider.Factory {

        private ChatInteractor chatInteractor;
        private EventBus eventBus;
        private LoggedInUser loggedInUser;
        private Context context;
        private UserStore userStore;
        private Gson gson;

        public Factory(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor, Gson gson) {
            this.context = context;
            this.loggedInUser = loggedInUser;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.gson = gson;
            this.chatInteractor = chatInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ChatViewModel.class)) {
                return (T) new ChatViewModel(context, eventBus, userStore, loggedInUser, chatInteractor, gson);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
