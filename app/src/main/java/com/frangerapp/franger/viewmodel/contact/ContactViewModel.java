package com.frangerapp.franger.viewmodel.contact;

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
import com.frangerapp.franger.R;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.model.ContactSyncResponse;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.ui.BaseBindingAdapters;
import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.common.databinding.FieldUtils;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pavanm on 10/03/18.
 */

public class ContactViewModel extends UserBaseViewModel {


    private User user;
    private Context context;
    private EventBus eventBus;
    private UserStore userStore;
    private ProfileInteractor profileInteractor;

    public ObservableBoolean showLoading = new ObservableBoolean(false);
    private List<ContactListItemViewModel> itemViewModels = new ArrayList<>();
    public ObservableField<List<ContactListItemViewModel>> inviteUserList = new ObservableField<>(itemViewModels);

    public ObservableField<String> searchTxt = new ObservableField<>("");

    public ContactViewModel(Context context, EventBus eventBus, UserStore userStore, User user, ProfileInteractor profileInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.userStore = userStore;
        this.profileInteractor = profileInteractor;
        this.user = user;
        initSearch();
        onContactsFetchCompleted();
    }


    public void checkForContactsPermission(Activity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.READ_CONTACTS)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        syncContacts(user);
                    } else {
                        // Oops permission denied
                        Toast.makeText(context, "permission Needed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void syncContacts(User user) {
        if (itemViewModels.size() <= 0)
            showLoading.set(true);
        profileInteractor.clearUsersList();
        profileInteractor.syncContacts(user.getUserId())
                .retry(2)
                .compose(SchedulerUtils.ioToMainObservableScheduler())
                .subscribe(this::onSuccess, this::onFailure, this::onContactsFetchCompleted);

    }

    private void onContactsFetchCompleted() {
        FRLogger.msg("onContactsFetchCompleted");

        profileInteractor.getSortedUsersList()
                .concatMapIterable(user -> user)
                .concatMap(user -> Observable.just(new ContactListItemViewModel(user)))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPhoneNumberAssociated, this::onAllPhoneNumbersAssociationFailed);
    }


    private void onAllPhoneNumbersAssociationFailed(Throwable throwable) {
        Toast.makeText(context, R.string.common_error_msg, Toast.LENGTH_SHORT).show();
    }

    private void onPhoneNumberAssociated(List<ContactListItemViewModel> inviteUserListItemViewModels) {
        showLoading.set(false);
        itemViewModels.addAll(inviteUserListItemViewModels);
        if (searchTxt.get().isEmpty())
            inviteUserList.set(itemViewModels);

    }

    private void onSuccess(ContactSyncResponse contactSyncResponse) {
        FRLogger.msg("onSuccess " + contactSyncResponse);

    }

    private void onFailure(Throwable throwable) {
        FRLogger.msg("onfailure " + throwable.getMessage());
        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public final BaseBindingAdapters.ItemClickHandler<ContactListItemViewModel> itemClickHandler = (position, item) -> {
        FRLogger.msg("item " + item);
//        CountriesViewEvent countriesViewEvent = CountriesViewEvent.builder();
//        countriesViewEvent.setId(CountriesPresentationConstants.COUNTRY_ITEM_CLICKED);
//        countriesViewEvent.setCountriesListItemViewModel(item);
//        eventBus.post(countriesViewEvent);
    };

    public void onQueryTextChanged(@Nullable String newText) {
        searchTxt.set(newText);
    }

    private void initSearch() {
        FieldUtils.toObservable(searchTxt)
                .debounce(30L, TimeUnit.MILLISECONDS)
                .subscribe(this::filterContacts, t -> FRLogger.msg("Error " + t.getMessage()));
    }

    private void filterContacts(String searchTxt) {
        FRLogger.msg("search Txt " + searchTxt);
        Observable.just(itemViewModels)
                .concatMapIterable(list -> list)
                .filter(contact -> searchTxt.isEmpty() || (contact.getDisplayName() != null && contact.getDisplayName().toLowerCase().contains(searchTxt.toLowerCase())))
                .toList()
                .compose(SchedulerUtils.ioToMainSingleScheduler())
                .subscribe(this::onListFiltered, t -> FRLogger.msg("Error " + t.getMessage()));
    }

    private void onListFiltered(List<ContactListItemViewModel> contactListItemViewModels) {
        if (contactListItemViewModels == null)
            contactListItemViewModels = new ArrayList<>();
        inviteUserList.set(contactListItemViewModels);
    }


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
            if (modelClass.isAssignableFrom(ContactViewModel.class)) {
                return (T) new ContactViewModel(context, eventBus, userStore, user, profileInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
