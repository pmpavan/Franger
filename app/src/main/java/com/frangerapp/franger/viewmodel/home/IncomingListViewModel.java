package com.frangerapp.franger.viewmodel.home;

import android.arch.lifecycle.MutableLiveData;
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
import com.frangerapp.franger.ui.BaseBindingAdapters;
import com.frangerapp.franger.ui.home.IncomingListItemUiState;
import com.frangerapp.franger.ui.home.OutgoingListItemUiState;
import com.frangerapp.franger.viewmodel.home.eventbus.IncomingListEvent;
import com.frangerapp.franger.viewmodel.home.util.HomePresentationConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pavanm on 17/03/18.
 */

public class IncomingListViewModel extends UserBaseViewModel implements IncomingListItemUiState.IncomingGroupItemClickHandler {
    private LoggedInUser loggedInUser;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ChatInteractor chatInteractor;

    private MutableLiveData<List<IncomingListItemUiState>> data = new MutableLiveData<>();

    private ArrayList<IncomingListItemUiState> items = new ArrayList<>();
    public ObservableField<String> messageTxt = new ObservableField<>("");

    IncomingListViewModel(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.loggedInUser = loggedInUser;
        this.data = new MutableLiveData<>();
        data.setValue(new ArrayList<>());
    }

    public void onPageLoaded() {
        pullChannelsFromDb();
        chatInteractor.getMessageEvent().subscribe(getChatObserver());
    }

    private void pullChannelsFromDb() {
        Disposable disposable = chatInteractor.getAnonListChannels()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMessagesFetched, this::onMessageFetchFailed);
        disposables.add(disposable);
    }

    private void onMessagesFetched(List<IncomingListItemUiState> outgoingListItemUiStates) {
        items.addAll(outgoingListItemUiStates);
//        FRLogger.msg("items onMessagesFetched " + items);
        getData().postValue(items);

    }

    private void onMessageFetchFailed(Throwable throwable) {
        FRLogger.msg("error onMessageFetchFailed " + throwable.getMessage());
    }

    private Observer<MessageEvent> getChatObserver() {
        return new Observer<MessageEvent>() {

            @Override
            public void onSubscribe(Disposable d) {
                FRLogger.msg(" First onSubscribe : " + d.isDisposed());
            }

            @Override
            public void onNext(MessageEvent messageEvent) {
                FRLogger.msg("home First onNext value : " + messageEvent);
                handleMessageEventReceived(messageEvent);
            }

            @Override
            public void onError(Throwable e) {
                FRLogger.msg(" First onError : " + e.getMessage());
            }

            @Override
            public void onComplete() {
                FRLogger.msg(" First onComplete");
            }
        };
    }

    private void handleMessageEventReceived(MessageEvent messageEvent) {

        if (messageEvent.isIncoming()) {
            if (messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.FEED.id
                    || messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id) {
                //update incoming/outgoing list and start a chat channel
                updateList(messageEvent);
            }
        }
    }

    private void updateList(MessageEvent messageEvent) {
        Disposable disposable = Observable.just(items)
                .flatMapIterable(incomingListItemUiStates -> incomingListItemUiStates)
                .filter(incomingListItemUiState -> incomingListItemUiState.getChannelName().equals(messageEvent.getChannel()))
                .map(incomingListItemUiState -> {
                    if (incomingListItemUiState.channelName.equals(messageEvent.getChannel())) {
                        if (messageEvent.isSentMessage())
                            incomingListItemUiState.setUnreadCount(0);
                        else
                            incomingListItemUiState.setUnreadCount(incomingListItemUiState.unreadCount + 1);
                        incomingListItemUiState.lastMessage = messageEvent.getMessage();
                        incomingListItemUiState.timeStamp = messageEvent.getTimestamp();
                    }
                    return incomingListItemUiState;
                })
                .switchIfEmpty(observer -> {
                    IncomingListItemUiState outgoingListItemUiState = new IncomingListItemUiState();
                    outgoingListItemUiState.setAnonymisedUserName(messageEvent.getAnonymisedUserName());
                    outgoingListItemUiState.setAnonymisedUserImg(messageEvent.getAnonymisedUserImg());
                    outgoingListItemUiState.setLastMessage(messageEvent.getMessage());
                    outgoingListItemUiState.setUserId(messageEvent.getUserId());
                    outgoingListItemUiState.setTimeStamp(messageEvent.getTimestamp());
                    outgoingListItemUiState.setUnreadCount(0);
                    outgoingListItemUiState.setChannelName(messageEvent.getChannel());
                    outgoingListItemUiState.setImageUrl("");
                    outgoingListItemUiState.setUser(messageEvent.getUser());
//                    items.add(0, outgoingListItemUiState);
                    observer.onNext(outgoingListItemUiState);
                })
                .toList()
                .map(this::sortArray)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMessagesFetched, this::onMessageFetchFailed);
        disposables.add(disposable);

    }

    private List<IncomingListItemUiState> sortArray(List<IncomingListItemUiState> sortedList) {
//        List<IncomingListItemUiState> sortedList = new ArrayList<>(unsortedList.size());
//        Collections.copy(sortedList, unsortedList);
        Collections.sort(sortedList, this::isDateGreater);
        return sortedList;
    }

    private int isDateGreater(IncomingListItemUiState incomingListItemUiState1, IncomingListItemUiState incomingListItemUiState2) {
        if (getTimestampInMillis(incomingListItemUiState1.getTimeStamp()) > getTimestampInMillis(incomingListItemUiState2.getTimeStamp())) {
            return -1;
        } else {
            return 1;
        }
    }

    private long getTimestampInMillis(Date dateTime) {
        return dateTime.getTime();
    }

    public MutableLiveData<List<IncomingListItemUiState>> getData() {
        return data;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    @Override
    public void onItemClick(IncomingListItemUiState item) {
        FRLogger.msg("item clicked IncomingListItemUiState " + item);
        ChatContact contact = new ChatContact(item.user);
        contact.setAnonymisedUserName(item.anonymisedUserName);
        contact.setAnonymisedUserImg(item.anonymisedUserImg);
        IncomingListEvent incomingListEvent = new IncomingListEvent();
        incomingListEvent.setId(HomePresentationConstants.ON_INCOMING_CHANNEL_CLICKED);
        incomingListEvent.setContact(contact);
        incomingListEvent.setIncoming(true);
        incomingListEvent.setChannelName(item.getChannelName());
        incomingListEvent.setChannelMutedOrBlocked(item.isUserBlocked);
        eventBus.post(incomingListEvent);
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
            if (modelClass.isAssignableFrom(IncomingListViewModel.class)) {
                return (T) new IncomingListViewModel(context, eventBus, userStore, loggedInUser, chatInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
