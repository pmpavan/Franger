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
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.home.IncomingListItemUiState;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
//        chatInteractor.getMessageEvent()
//                .subscribe(getChatMsgObserver());

        pullChannelsFromDb();
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
        FRLogger.msg("items onMessagesFetched " + items);
        getData().postValue(items);

    }

    private void onMessageFetchFailed(Throwable throwable) {
        FRLogger.msg("error onMessageFetchFailed " + throwable.getMessage());
    }

    public MutableLiveData<List<IncomingListItemUiState>> getData() {
        return data;
    }

    @Override
    public void onItemClick(IncomingListItemUiState model) {
        FRLogger.msg("item clicked OutgoingListUiState " + model);

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
