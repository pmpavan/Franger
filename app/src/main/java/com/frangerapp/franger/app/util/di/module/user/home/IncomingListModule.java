package com.frangerapp.franger.app.util.di.module.user.home;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.FragmentScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.home.IncomingListAdapter;
import com.frangerapp.franger.ui.home.IncomingListUiState;
import com.frangerapp.franger.viewmodel.home.IncomingListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

@Module
public class IncomingListModule {


    public IncomingListModule() {
    }

    @Provides
    @FragmentScope
    IncomingListAdapter getIncomingListAdapter(Context context, @NonNull LoggedInUser loggedInUser) {
        return new IncomingListAdapter(context, loggedInUser);
    }

    @Provides
    @FragmentScope
    IncomingListUiState getIncomingListUiState() {
        return new IncomingListUiState();
    }

    @Provides
    @FragmentScope
    ViewModelProvider.Factory incomingListFragmentViewModel(@NonNull Context context, @NonNull LoggedInUser loggedInUser, @NonNull UserStore userStore, @NotNull EventBus eventBus, @NotNull ChatInteractor chatInteractor) {
        return new IncomingListViewModel.Factory(context, eventBus, userStore, loggedInUser, chatInteractor);
    }
}
