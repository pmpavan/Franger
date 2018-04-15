package com.frangerapp.franger.viewmodel.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.R;
import com.frangerapp.franger.app.util.db.entity.AnonListChannel;
import com.frangerapp.franger.app.util.db.entity.Message;
import com.frangerapp.franger.app.util.db.entity.MyListChannel;
import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.viewmodel.chat.eventbus.ChatEvent;
import com.frangerapp.franger.viewmodel.chat.util.ChatPresentationConstants;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

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

public class ChatViewModel extends UserBaseViewModel implements ChatListItemUiState.ChatItemClickHandler {

    private LoggedInUser loggedInUser;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private ChatContact chatContact;
    private boolean isIncoming;
    private String channelName;

    private boolean isChannelBlocked = false;
    private boolean isChannelMuted = false;
    public ObservableBoolean scrollToBottom = new ObservableBoolean(true);

    public MutableLiveData<String> getTitleTxt() {
        return titleTxt;
    }

    private MutableLiveData<String> titleTxt = new MutableLiveData<>();

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
        this.titleTxt = new MutableLiveData<>();
        data.setValue(new ArrayList<>());
        titleTxt.setValue("");
    }

    public void onViewLoaded(ChatContact chatContact, boolean isIncoming, String channelName, boolean isChannelBlockedOrMuted) {
        this.isIncoming = isIncoming;
        this.chatContact = chatContact;
        this.channelName = channelName;
        if (channelName == null || channelName.isEmpty()) {
            this.channelName = chatInteractor.getChatName(chatContact.getUserId(), isIncoming);
        }
        if (isIncoming) {
            isChannelBlocked = isChannelBlockedOrMuted;
        } else {
            isChannelMuted = isChannelBlockedOrMuted;
        }
        FRLogger.msg("channel name in chat page " + channelName);
        sendSetToolbarTitleTxtEvent();
        chatInteractor.getMessageEvent()
                .subscribe(getChatMsgObserver());

    }

    public void onPageLoaded() {
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
        items.clear();
        items.addAll(chatListItemUiStates);
//        FRLogger.msg("items onMessagesFetched " + items);
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
        String userName = chatContact.getDisplayName();
        if (isIncoming)
            userName = chatContact.getAnonymisedUserName();
        titleTxt.postValue(userName + " ");
//        ChatEvent event = new ChatEvent();
//        event.setId(ChatPresentationConstants.SET_TOOLBAR_TXT);
//        event.setMessage(userName);
//        eventBus.post(event);
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
        FRLogger.msg("chat page message Event " + messageEvent.getChannel());
        if (messageEvent.getChannel().equalsIgnoreCase(channelName) && !messageEvent.isSentMessage()) {
            if (messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.FEED.id
                    || messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id) {
                // update in list
                FRLogger.msg("received the message event in chat page " + messageEvent.getUser());
                addMsgToAdapter(messageEvent.getMessage(), messageEvent.getUserId(), messageEvent.getTimestamp(), messageEvent.getUser(), messageEvent.getMessageId());
            }
        }
    }

    public void onSendButtonClicked() {
        if (!Objects.requireNonNull(getMessage()).isEmpty()) {
            long messageId = chatInteractor.sendMessage(chatContact.getUserId(), isIncoming, getMessage());
            addMsgToAdapter(messageId);
            messageTxt.set("");
        }
    }

    private String getMessage() {
        return messageTxt.get().trim();
    }

    public MutableLiveData<List<ChatListItemUiState>> getData() {
        return data;
    }

    private void addMsgToAdapter(String message, String userId, Date timeStamp, User user,
                                 long messageId) {
        ChatListItemUiState chatListItemUiState = new ChatListItemUiState();
        chatListItemUiState.setMessage(message);
        chatListItemUiState.setUserId(userId);
        chatListItemUiState.setUser(user);
        chatListItemUiState.setMessageId(messageId);
        chatListItemUiState.setTimeStamp(timeStamp);
        items.add(chatListItemUiState);
//        FRLogger.msg("items " + items);
        getData().postValue(items);
    }

    private void addMsgToAdapter(long messageId) {
        User user = new User();
        FRLogger.msg("user ID " + loggedInUser.getUserId());
        user.userId = loggedInUser.getUserId();
        user.phoneNumber = loggedInUser.getPhoneNumber();
        user.displayName = loggedInUser.getUserName();
        addMsgToAdapter(getMessage(), loggedInUser.getUserId(), new Date(), user, messageId);
    }

    @Override
    public void onItemClick(int position, ChatListItemUiState model) {
        FRLogger.msg("onItemClicked chat " + model);

    }

    public void onBlockPressed() {
        if (isIncoming) {
            chatInteractor.blockChannel(channelName, isChannelBlocked);
            Disposable disposable = chatInteractor.blockChannel(channelName, isChannelBlocked)
                    .toObservable()
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(this::onBlockSuccess, this::onError);
            disposables.add(disposable);
        } else {
            Disposable disposable = chatInteractor.muteChannel(channelName, isChannelMuted)
                    .toObservable()
                    .compose(SchedulerUtils.ioToMainObservableScheduler())
                    .subscribe(this::onMuteSuccess, this::onError);
            disposables.add(disposable);
        }
    }

    public void onDeletePressed() {
        items.clear();
        getData().postValue(items);
        if (isIncoming) {
            chatInteractor.clearIncomingChannelChat(channelName);
        } else {
            chatInteractor.clearOutgoingChannelChat(channelName);

        }
    }

    private void onMuteSuccess(MyListChannel myListChannel) {
        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setId(ChatPresentationConstants.ON_MUTE_CLKD);
        eventBus.post(chatEvent);
    }

    private void onError(Throwable throwable) {

    }

    private void onBlockSuccess(AnonListChannel anonListChannel) {
        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setId(ChatPresentationConstants.ON_BLOCK_CLKD);
        eventBus.post(chatEvent);
    }

    @NotNull
    public int getBlockMenuTitle() {
        if (isIncoming) {
            if (isChannelBlocked)
                return R.string.block;
            else {
                return R.string.unblock;
            }
        } else {
            if (isChannelMuted)
                return R.string.mute;
            else {
                return R.string.unmute;
            }
        }
    }

    @NotNull
    public boolean getBlockMenuVisibility() {
        return !(isIncoming && !isChannelBlocked);
    }

    @NotNull
    public int getBlockMenuIcon() {
        if (isIncoming) {
            if (isChannelBlocked)
                return R.drawable.ic_block_black_24dp;
            else {
                return R.drawable.ic_send_black_24dp;
            }
        } else {
            if (isChannelMuted)
                return R.drawable.ic_volume_mute_black_24dp;
            else {
                return R.drawable.ic_volume_up_black_24dp;
            }
        }
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
