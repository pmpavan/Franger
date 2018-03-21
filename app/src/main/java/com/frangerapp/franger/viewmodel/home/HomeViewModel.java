package com.frangerapp.franger.viewmodel.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
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
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.domain.chat.model.FeedNewMessageResponse;
import com.frangerapp.franger.viewmodel.contact.ContactListItemViewModel;
import com.frangerapp.franger.viewmodel.home.eventbus.HomeEvent;
import com.frangerapp.franger.viewmodel.home.util.HomePresentationConstants;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Pavan on 24/01/18.
 */

public class HomeViewModel extends BaseViewModel implements ChatPresentationImpl.ChatPresentationCallbacks {

    private EventBus eventBus;
    private User user;
    private Context context;
    private UserStore userStore;
    private ChatInteractor chatInteractor;
    private Gson gson;

    public HomeViewModel(Context context, User user, EventBus eventBus, UserStore userStore, ChatInteractor chatInteractor, Gson gson) {
        this.context = context;
        this.user = user;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.chatInteractor = chatInteractor;
        this.gson = gson;
        chatInteractor.setCallbacks(this);
        chatInteractor.addFeedEvent();
    }

    public void onFabClicked() {
        HomeEvent event = new HomeEvent();
        event.setId(HomePresentationConstants.ON_FAB_CLICKED);
        eventBus.post(event);
    }


    public void onPageLoaded() {
        chatInteractor.getMessageEvent().subscribe(getFirstObserver());
    }

    @Override
    public void onViewDestroyed() {
        super.onViewDestroyed();

    }

    @Override
    public void onChatInitiateEventReceived(FeedNewMessageResponse feedNewMessageResponse, String channelName, boolean isIncoming) {
        FRLogger.msg("home message is $args " + feedNewMessageResponse.getChannel());
        chatInteractor.addChatEvent(channelName, isIncoming);
    }

    @Override
    public void onChatMessageEventReceived(ChatMessage chatMessage) {
        FRLogger.msg("home message is $args ${json.channel}" + chatMessage.getChannel());
    }

    private Observer<MessageEvent> getFirstObserver() {
        return new Observer<MessageEvent>() {

            @Override
            public void onSubscribe(Disposable d) {
                FRLogger.msg(" First onSubscribe : " + d.isDisposed());
            }

            @Override
            public void onNext(MessageEvent messageEvent) {
                FRLogger.msg("home First onNext value : " + messageEvent);
//                Toast.makeText(context, "home " + messageEvent.toString(), Toast.LENGTH_SHORT).show();
                HomeEvent event = new HomeEvent();
                event.setId(1);
                eventBus.post(event);
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

    public static class Factory implements ViewModelProvider.Factory {

        private EventBus eventBus;
        private User user;
        private Context context;
        private UserStore userStore;
        private ChatInteractor chatInteractor;
        private Gson gson;

        public Factory(Context context, EventBus eventBus, UserStore userStore, User user, ChatInteractor chatInteractor, Gson gson) {
            this.context = context;
            this.user = user;
            this.gson = gson;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.chatInteractor = chatInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(HomeViewModel.class)) {
                return (T) new HomeViewModel(context, user, eventBus, userStore, chatInteractor, gson);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
