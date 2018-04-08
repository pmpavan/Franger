package com.frangerapp.franger.viewmodel.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.chat.model.MessageEvent;
import com.frangerapp.franger.domain.chat.util.ChatDataConstants;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.viewmodel.home.eventbus.HomeEvent;
import com.frangerapp.franger.viewmodel.home.util.HomePresentationConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Pavan on 24/01/18.
 */

public class HomeViewModel extends UserBaseViewModel {

    private EventBus eventBus;
    private LoggedInUser loggedInUser;
    private Context context;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private Gson gson;

    public HomeViewModel(Context context, LoggedInUser loggedInUser, EventBus eventBus, UserStore userStore, ChatInteractor chatInteractor, Gson gson) {
        this.context = context;
        this.loggedInUser = loggedInUser;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.gson = gson;
        chatInteractor.addFeedEvent();
    }

    public void onFabClicked() {
        HomeEvent event = new HomeEvent();
        event.setId(HomePresentationConstants.ON_FAB_CLICKED);
        eventBus.post(event);
    }


    public void onPageLoaded() {
        chatInteractor.getMessageEvent().subscribe(getChatObserver());
    }

    @Override
    public void onViewDestroyed() {
        super.onViewDestroyed();

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

        if (messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.FEED.id) {
            //update incoming/outgoing list and start a chat channel
        } else if (messageEvent.getEventType() == ChatDataConstants.SOCKET_EVENT_TYPE.MESSAGE.id) {

        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getChatObserver().onComplete();
        chatInteractor.onAppClosed();
    }

    public static class Factory implements ViewModelProvider.Factory {

        private EventBus eventBus;
        private LoggedInUser loggedInUser;
        private Context context;
        private UserStore userStore;
        private ChatInteractor chatInteractor;
        private Gson gson;

        public Factory(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ChatInteractor chatInteractor, Gson gson) {
            this.context = context;
            this.loggedInUser = loggedInUser;
            this.gson = gson;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.chatInteractor = chatInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(HomeViewModel.class)) {
                return (T) new HomeViewModel(context, loggedInUser, eventBus, userStore, chatInteractor, gson);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
