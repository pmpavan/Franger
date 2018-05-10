package com.frangerapp.franger.viewmodel.invite;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.model.Joined;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.BaseBindingAdapters;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.frangerapp.franger.viewmodel.invite.eventbus.InviteUserEvent;
import com.frangerapp.franger.viewmodel.invite.util.InviteUserPresentationConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Pavan on 07/02/18.
 */

public class InviteUserViewModel extends UserBaseViewModel {


    private LoggedInUser loggedInUser;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ProfileInteractor profileInteractor;

    public ObservableBoolean showLoading = new ObservableBoolean(false);
    public List<InviteUserListItemViewModel> itemViewModels = new ArrayList<>();
    public ObservableField<List<InviteUserListItemViewModel>> inviteUserList = new ObservableField<>(itemViewModels);


    InviteUserViewModel(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ProfileInteractor profileInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.profileInteractor = profileInteractor;
        this.loggedInUser = loggedInUser;
    }

    public void checkForContactsPermission(Activity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        Disposable disposable = rxPermissions.request(Manifest.permission.READ_CONTACTS)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        syncContacts(loggedInUser);
                    } else {
                        // Oops permission denied
                        Toast.makeText(context, "Permission Needed", Toast.LENGTH_SHORT).show();
                    }
                }, Throwable::printStackTrace);
        disposables.add(disposable);
    }

    private void syncContacts(LoggedInUser loggedInUser) {
        showLoading.set(true);
        profileInteractor.clearUsersList();
        Disposable disposable= profileInteractor.syncContacts(loggedInUser.getUserId())
                .retry(2)
                .compose(SchedulerUtils.ioToMainObservableScheduler())
                .subscribe(this::onSuccess, this::onFailure, this::onComplete);
        disposables.add(disposable);

    }

    private void onComplete() {
        FRLogger.msg("onComplete");
        itemViewModels = new ArrayList<>();
        Disposable disposable = profileInteractor.getSortedUsersList()
                .toObservable()
                .concatMapIterable(user -> user)
                .concatMap(user -> Observable.just(new InviteUserListItemViewModel(user)))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPhoneNumberAssociated, this::onAllPhoneNumbersAssociationFailed);
        disposables.add(disposable);
    }


    private void onAllPhoneNumbersAssociationFailed(Throwable throwable) {

    }

    private void onPhoneNumberAssociated(List<InviteUserListItemViewModel> inviteUserListItemViewModels) {
        FRLogger.msg("invite loggedInUser list " + inviteUserListItemViewModels);
        showLoading.set(false);
        itemViewModels.addAll(inviteUserListItemViewModels);
        inviteUserList.set(itemViewModels);

    }

    private void onSuccess(List<Joined> contactSyncResponse) {
        FRLogger.msg("onSuccess " + contactSyncResponse);

    }

    private void onFailure(Throwable throwable) {
        FRLogger.msg("onfailure " + throwable.getMessage());
        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void onNextClicked() {
        profileInteractor.setUserInviteCompleted();
        InviteUserEvent event = new InviteUserEvent();
        event.setId(InviteUserPresentationConstants.ON_NEXT_BTN_CLICKED);
        eventBus.post(event);
    }

    public final BaseBindingAdapters.ItemClickHandler<InviteUserListItemViewModel> itemClickHandler = (item) -> {
        FRLogger.msg("invite item " + item);
//        CountriesViewEvent countriesViewEvent = CountriesViewEvent.builder();
//        countriesViewEvent.setId(CountriesPresentationConstants.COUNTRY_ITEM_CLICKED);
//        countriesViewEvent.setCountriesListItemViewModel(item);
//        eventBus.post(countriesViewEvent);
    };

    public static class Factory implements ViewModelProvider.Factory {

        private ProfileInteractor profileInteractor;
        private EventBus eventBus;
        private LoggedInUser loggedInUser;
        private Context context;
        private UserStore userStore;

        public Factory(Context context, EventBus eventBus, UserStore userStore, LoggedInUser loggedInUser, ProfileInteractor profileInteractor) {
            this.context = context;
            this.loggedInUser = loggedInUser;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.profileInteractor = profileInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(InviteUserViewModel.class)) {
                return (T) new InviteUserViewModel(context, eventBus, userStore, loggedInUser, profileInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }

}
