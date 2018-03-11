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

import com.crashlytics.android.answers.InviteEvent;
import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.model.ContactSyncResponse;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
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
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Pavan on 07/02/18.
 */

public class InviteUserViewModel extends UserBaseViewModel {


    private User user;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ProfileInteractor profileInteractor;

    public ObservableBoolean showLoading = new ObservableBoolean(false);
    public List<InviteUserListItemViewModel> itemViewModels = new ArrayList<>();
    public ObservableField<List<InviteUserListItemViewModel>> inviteUserList = new ObservableField<>(itemViewModels);


    public InviteUserViewModel(Context context, EventBus eventBus, UserStore userStore, User user, ProfileInteractor profileInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.profileInteractor = profileInteractor;
        this.user = user;
    }

    public void checkForContactsPermission(Activity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.READ_CONTACTS)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        syncContacts(user);
                    } else {
                        // Oups permission denied
                        Toast.makeText(context, "permission Needed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void syncContacts(User user) {
        showLoading.set(true);
        profileInteractor.clearUsersList();
        profileInteractor.syncContacts(user.getUserId())
                .retry(2)
                .compose(SchedulerUtils.ioToMainObservableScheduler())
                .subscribe(this::onSuccess, this::onFailure, this::onComplete);

    }

    private void onComplete() {
        FRLogger.msg("onComplete");

        profileInteractor.getSortedUsersList()
                .concatMapIterable(user -> user)
                .concatMap(user -> Observable.just(new InviteUserListItemViewModel(user)))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPhoneNumberAssociated, this::onAllPhoneNumbersAssociationFailed);
    }


    private void onAllPhoneNumbersAssociationFailed(Throwable throwable) {

    }

    private void onPhoneNumberAssociated(List<InviteUserListItemViewModel> inviteUserListItemViewModels) {
        FRLogger.msg("invite user list " + inviteUserListItemViewModels);
        showLoading.set(false);
        itemViewModels.addAll(inviteUserListItemViewModels);
        inviteUserList.set(itemViewModels);

    }

    private void onSuccess(ContactSyncResponse contactSyncResponse) {
        FRLogger.msg("onSuccess " + contactSyncResponse);

    }

    private void onFailure(Throwable throwable) {
        FRLogger.msg("onfailure " + throwable.getMessage());
        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void onNextClicked(){
        profileInteractor.setUserInviteCompleted();
        InviteUserEvent event =new InviteUserEvent();
        event.setId(InviteUserPresentationConstants.ON_NEXT_BTN_CLICKED);
        eventBus.post(event);
    }

    public final BaseBindingAdapters.ItemClickHandler<InviteUserListItemViewModel> itemClickHandler = (position, item) -> {
        FRLogger.msg("item " + item);
//        CountriesViewEvent countriesViewEvent = CountriesViewEvent.builder();
//        countriesViewEvent.setId(CountriesPresentationConstants.COUNTRY_ITEM_CLICKED);
//        countriesViewEvent.setCountriesListItemViewModel(item);
//        eventBus.post(countriesViewEvent);
    };

    public static class Factory implements ViewModelProvider.Factory {

        private ProfileInteractor profileInteractor;
        private EventBus eventBus;
        private User user;
        private Context context;
        private UserStore userStore;

        public Factory(Context context, EventBus eventBus, UserStore userStore, User user, ProfileInteractor profileInteractor) {
            this.context = context;
            this.user = user;
            this.eventBus = eventBus;
            this.userStore = userStore;
            this.profileInteractor = profileInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(InviteUserViewModel.class)) {
                return (T) new InviteUserViewModel(context, eventBus, userStore, user, profileInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }

}
