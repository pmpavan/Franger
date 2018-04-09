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
import com.frangerapp.franger.viewmodel.home.eventbus.OutgoingListEvent;
import com.frangerapp.franger.viewmodel.home.util.HomePresentationConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pavanm on 17/03/18.
 */

public class OutgoingListViewModel extends UserBaseViewModel implements OutgoingListItemUiState.OutgoingGroupItemClickHandler {
    private LoggedInUser loggedInUser;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ChatInteractor chatInteractor;

    private MutableLiveData<List<OutgoingListItemUiState>> data = new MutableLiveData<>();

    private ArrayList<OutgoingListItemUiState> items = new ArrayList<>();
    public ObservableField<String> messageTxt = new ObservableField<>("");

    OutgoingListViewModel(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor) {
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
        Disposable disposable = chatInteractor.getMyListChannels()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMessagesFetched, this::onMessageFetchFailed);
        disposables.add(disposable);
    }

    private void onMessagesFetched(List<OutgoingListItemUiState> outgoingListItemUiStates) {
        items.addAll(outgoingListItemUiStates);
        FRLogger.msg("items onMessagesFetched " + items);
        getData().postValue(items);

    }

    private void onMessageFetchFailed(Throwable throwable) {
        FRLogger.msg("error onMessageFetchFailed " + throwable.getMessage());
    }

    public MutableLiveData<List<OutgoingListItemUiState>> getData() {
        return data;
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

        if (!messageEvent.isIncoming()) {
            if (messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.FEED.id) {
                //update outgoing list and start a chat channel
            } else if (messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id) {

            }
        }
    }

    @Override
    public void onItemClick(OutgoingListItemUiState model) {
        FRLogger.msg("item clicked OutgoingListUiState " + model);

    }

    public final BaseBindingAdapters.ItemClickHandler<OutgoingListItemUiState> itemClickHandler = (model) -> {
        FRLogger.msg("item clicked " + model);
        OutgoingListEvent incomingListEvent = new OutgoingListEvent();
        incomingListEvent.setId(HomePresentationConstants.ON_OUTGOING_CHANNEL_CLICKED);
        incomingListEvent.setContact(new ChatContact(model.user));
        incomingListEvent.setIncoming(false);
        incomingListEvent.setChannelName(model.getChannelName());
        eventBus.post(incomingListEvent);
    };

    @Override
    protected void onCleared() {
        super.onCleared();
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
            if (modelClass.isAssignableFrom(OutgoingListViewModel.class)) {
                return (T) new OutgoingListViewModel(context, eventBus, userStore, loggedInUser, chatInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
