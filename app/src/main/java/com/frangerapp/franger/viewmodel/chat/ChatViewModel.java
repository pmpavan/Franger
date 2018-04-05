package com.frangerapp.franger.viewmodel.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.app.util.db.entity.Message;
import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.chat.ChatListUiState;
import com.frangerapp.franger.viewmodel.chat.eventbus.ChatEvent;
import com.frangerapp.franger.viewmodel.chat.util.ChatPresentationConstants;
import com.frangerapp.franger.viewmodel.contact.ContactListItemViewModel;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pavanm on 13/03/18.
 */

public class ChatViewModel extends UserBaseViewModel implements ChatListUiState.ActionClickHandler {

    private LoggedInUser loggedInUser;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private ProfileInteractor profileInteractor;
    private ChatContact chatContact;
    private boolean isIncoming;
    private String channelName;

    private MutableLiveData<List<ChatListItemUiState>> data = new MutableLiveData<>();

    private ArrayList<ChatListItemUiState> items = new ArrayList<>();
    public ObservableField<String> messageTxt = new ObservableField<>("");

    ChatViewModel(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor, ProfileInteractor profileInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.loggedInUser = loggedInUser;
        this.profileInteractor = profileInteractor;
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

        pullMessagesFromDb();
    }

    private void pullMessagesFromDb() {
        Disposable disposable = chatInteractor.getMessages(chatContact.getUserId(), isIncoming)
                .toObservable()
                .concatMapIterable(user -> user)
                .concatMap(user -> Observable.just(getChatListItemUiState(user)))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMessagesFetched, this::onMessageFetchFailed);
        disposables.add(disposable);
    }

    private void onMessageFetchFailed(Throwable throwable) {
        FRLogger.msg("error " + throwable.getMessage());
    }

    private void onMessagesFetched(List<ChatListItemUiState> chatListItemUiStates) {
        items.addAll(chatListItemUiStates);
        FRLogger.msg("items onMessagesFetched " + items);
        getData().postValue(items);
    }

    private ChatListItemUiState getChatListItemUiState(Message messageObj) {
        ChatListItemUiState chatListItemUiState = new ChatListItemUiState();
        chatListItemUiState.setMessage(messageObj.message);
        chatListItemUiState.setUserId(messageObj.userId);
//        chatListItemUiState.setUser(user);
        chatListItemUiState.setMessageId(messageObj.id);
        chatListItemUiState.setTimeStamp(messageObj.sentAt);
        return chatListItemUiState;
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
            FRLogger.msg("received the message event in chat page " + messageEvent.getUser());
            addMsgToAdapter(messageEvent.getMessage(), messageEvent.getUserId(), messageEvent.getTimestamp(), messageEvent.getUser(), messageEvent.getMessageId());
        }
    }

    public void onSendButtonClicked() {
        if (!Objects.requireNonNull(messageTxt.get()).isEmpty()) {
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
        chatListItemUiState.setUserId(userId);
        chatListItemUiState.setUser(user);
        chatListItemUiState.setMessageId(messageId);
        chatListItemUiState.setTimeStamp(timeStamp);
        items.add(chatListItemUiState);
        FRLogger.msg("items " + items);
        getData().postValue(items);
    }

    private void addMsgToAdapter(long messageId) {
        User user = new User();
        FRLogger.msg("user ID " + loggedInUser.getUserId());
        user.userId = loggedInUser.getUserId();
        user.phoneNumber = loggedInUser.getPhoneNumber();
        user.displayName = loggedInUser.getUserName();
        addMsgToAdapter(messageTxt.get(), loggedInUser.getUserId(), new Date(), user, messageId);
    }

    @Override
    public void onItemClick(ChatListUiState chatListUiState) {
        FRLogger.msg("onItemClicked chat " + chatListUiState);
    }

    public static class Factory implements ViewModelProvider.Factory {

        private ChatInteractor chatInteractor;
        private EventBus eventBus;
        private LoggedInUser loggedInUser;
        private Context context;
        private UserStore userStore;
        private ProfileInteractor profileInteractor;

        public Factory(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor, ProfileInteractor profileInteractor) {
            this.context = context;
            this.loggedInUser = loggedInUser;
            this.eventBus = eventBus;
            this.profileInteractor = profileInteractor;
            this.userStore = userStore;
            this.chatInteractor = chatInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ChatViewModel.class)) {
                return (T) new ChatViewModel(context, eventBus, userStore, loggedInUser, chatInteractor, profileInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
