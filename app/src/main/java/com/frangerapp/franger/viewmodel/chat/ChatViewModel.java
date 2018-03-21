package com.frangerapp.franger.viewmodel.chat;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.interactor.impl.ChatPresentationImpl;
import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.domain.chat.model.ChatMessage;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.viewmodel.chat.eventbus.ChatEvent;
import com.frangerapp.franger.domain.chat.model.FeedNewMessageResponse;
import com.frangerapp.franger.viewmodel.chat.util.ChatPresentationConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by pavanm on 13/03/18.
 */

public class ChatViewModel extends UserBaseViewModel implements ChatPresentationImpl.ChatPresentationCallbacks {

    private User user;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private ChatContact chatContact;
    private boolean isIncoming;
    private String channelName;
    private Gson gson;

    public ObservableField<String> messageTxt = new ObservableField<>("");

    public ChatViewModel(Context context, EventBus eventBus, UserStore userStore, User user, ChatInteractor chatInteractor, Gson gson) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.user = user;
        this.gson = gson;
        chatInteractor.setCallbacks(this);
    }

    public void onPageLoaded(ChatContact chatContact, boolean isIncoming, String channelName) {
        this.isIncoming = isIncoming;
        this.chatContact = chatContact;
        this.channelName = channelName;
        if (channelName == null || channelName.isEmpty()) {
            this.channelName = chatInteractor.getChatEventName(chatContact.getUserId(), isIncoming);
        }
        sendSetToolbarTitleTxtEvent();
        chatInteractor.getMessageEvent().subscribe(getFirstObserver());
    }

    private void sendSetToolbarTitleTxtEvent() {
        ChatEvent event = new ChatEvent();
        event.setId(ChatPresentationConstants.SET_TOOLBAR_TXT);
        event.setMessage(chatContact.getDisplayName());
        eventBus.post(event);
    }

    private Observer<MessageEvent> getFirstObserver() {
        return new Observer<MessageEvent>() {

            @Override
            public void onSubscribe(Disposable d) {
                FRLogger.msg("chat First onSubscribe : " + d.isDisposed());
            }

            @Override
            public void onNext(MessageEvent messageEvent) {
                FRLogger.msg("chat First onNext value : " + messageEvent);
//                Toast.makeText(context, "chat " + messageEvent.toString(), Toast.LENGTH_SHORT).show();
//                chatInteractor.sendMessage(messageEvent.getChannel(), isIncoming, "new message");
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

    public void onSendButtonClicked() {
        if (!messageTxt.get().isEmpty()) {
            chatInteractor.addChatEvent(chatContact.getUserId(), isIncoming);
            chatInteractor.sendMessage(chatContact.getUserId(), isIncoming, messageTxt.get());
            messageTxt.set("");
        }
    }

    @Override
    public void onChatInitiateEventReceived(FeedNewMessageResponse feedNewMessageResponse, String channelName, boolean isIncoming) {
        FRLogger.msg("chat message is $args " + feedNewMessageResponse.getChannel());
        chatInteractor.addChatEvent(channelName, isIncoming);
        chatInteractor.sendMessage(channelName, isIncoming, "received message");
    }

    @Override
    public void onChatMessageEventReceived(ChatMessage chatMessage) {
        FRLogger.msg("chat message is $args ${json.channel}" + chatMessage.getChannel());
    }

    public static class Factory implements ViewModelProvider.Factory {

        private ChatInteractor chatInteractor;
        private EventBus eventBus;
        private User user;
        private Context context;
        private UserStore userStore;
        private Gson gson;

        public Factory(Context context, EventBus eventBus, UserStore userStore, User user, ChatInteractor chatInteractor, Gson gson) {
            this.context = context;
            this.user = user;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.gson = gson;
            this.chatInteractor = chatInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ChatViewModel.class)) {
                return (T) new ChatViewModel(context, eventBus, userStore, user, chatInteractor, gson);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
