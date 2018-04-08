package com.frangerapp.franger.app.util.di.module.user.home;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.FragmentScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.chat.interactor.ChatInteractor;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.home.OutgoingListAdapter;
import com.frangerapp.franger.ui.home.OutgoingListUiState;
import com.frangerapp.franger.viewmodel.home.OutgoingListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

@Module
public class OutgoingListModule {


    public OutgoingListModule() {
    }

    @Provides
    @FragmentScope
    OutgoingListAdapter getOutgoingListAdapter(Context context, @NonNull LoggedInUser loggedInUser) {
        return new OutgoingListAdapter(context, loggedInUser);
    }

    @Provides
    @FragmentScope
    OutgoingListUiState getOutgoingListUiState() {
        return new OutgoingListUiState();
    }

    @Provides
    @FragmentScope
    ViewModelProvider.Factory outgoingListFragmentViewModel(@NonNull Context context, @NonNull LoggedInUser loggedInUser, @NonNull UserStore userStore, @NotNull EventBus eventBus, @NotNull ChatInteractor chatInteractor) {
        return new OutgoingListViewModel.Factory(context, eventBus, userStore, loggedInUser, chatInteractor);
    }
}
