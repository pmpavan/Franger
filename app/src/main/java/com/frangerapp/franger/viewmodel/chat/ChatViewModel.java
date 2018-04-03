package com.frangerapp.franger.viewmodel.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.chat.ChatListUiState;
import com.frangerapp.franger.viewmodel.chat.eventbus.ChatEvent;
import com.frangerapp.franger.viewmodel.chat.util.ChatPresentationConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by pavanm on 13/03/18.
 */

public class ChatViewModel extends UserBaseViewModel implements ChatListUiState.ActionClickHandler {

    private LoggedInUser loggedInUser;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private ChatContact chatContact;
    private boolean isIncoming;
    private String channelName;

    private MutableLiveData<List<ChatListItemUiState>> data = new MutableLiveData<>();

    private ArrayList<ChatListItemUiState> items = new ArrayList<>();
    public ObservableField<String> messageTxt = new ObservableField<>("");

    ChatViewModel(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.loggedInUser = loggedInUser;
        this.data = new MutableLiveData<>();
        data.setValue(new ArrayList<>());
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
        //TODO
        channelName = "chat_1_2";
        if (messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id
                && messageEvent.getChannel().equalsIgnoreCase(channelName)) {
            // update in list
            FRLogger.msg("received the message event in chat page " + messageEvent.getUser());
            addMsgToAdapter(messageEvent.getMessage(), messageEvent.getUserId(), messageEvent.getTimestamp(), messageEvent.getUser(), messageEvent.getMessageId());
        }
    }

    public void onSendButtonClicked() {
        if (!messageTxt.get().isEmpty()) {
//            chatInteractor.addChatEvent(chatContact.getUserId(), isIncoming);
            long messageId = chatInteractor.sendMessage(chatContact.getUserId(), isIncoming, messageTxt.get());
            addMsgToAdapter(messageId);
            messageTxt.set("");
        }
    }

    public MutableLiveData<List<ChatListItemUiState>> getData() {
        return data;
    }

    private void addMsgToAdapter(String message, String userId, Date timeStamp, User user, long messageId) {
        ChatListItemUiState chatListItemUiState = new ChatListItemUiState();
        chatListItemUiState.setMessage(message);
        chatListItemUiState.setType(isIncoming ? ChatListItemUiState.CHAT_ITEM_TYPE.INCOMING : ChatListItemUiState.CHAT_ITEM_TYPE.OUTGOING);
        chatListItemUiState.setUserId(userId);
        chatListItemUiState.setUser(user);
        chatListItemUiState.setMessageId(messageId);
        chatListItemUiState.setTimeStamp(timeStamp);
        items.add(chatListItemUiState);
        FRLogger.msg("items " + items);
        data.postValue(items);
    }

    private void addMsgToAdapter(long messageId) {
        User user = new User();
        user.userId = loggedInUser.getUserId();
        user.phoneNumber = loggedInUser.getPhoneNumber();
        user.displayName = loggedInUser.getUserName();
        addMsgToAdapter(messageTxt.get(), loggedInUser.getUserId(), new Date(), user, messageId);
    }

    @Override
    public void onItemClick() {
        FRLogger.msg("onItemClicked chat");
    }

    public static class Factory implements ViewModelProvider.Factory {

        private ChatInteractor chatInteractor;
        private EventBus eventBus;
        private LoggedInUser loggedInUser;
        private Context context;
        private UserStore userStore;

        public Factory(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor) {
            this.context = context;
            this.loggedInUser = loggedInUser;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.chatInteractor = chatInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ChatViewModel.class)) {
                return (T) new ChatViewModel(context, eventBus, userStore, loggedInUser, chatInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
