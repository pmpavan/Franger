package com.frangerapp.franger.viewmodel.contact;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.R;
import com.frangerapp.franger.data.profile.model.Joined;
import com.frangerapp.franger.domain.chat.model.ChatContact;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.LoggedInUser;
import com.frangerapp.franger.ui.BaseBindingAdapters;
import com.frangerapp.franger.viewmodel.common.databinding.FieldUtils;
import com.frangerapp.franger.viewmodel.common.rx.SchedulerUtils;
import com.frangerapp.franger.viewmodel.contact.eventbus.ContactEvent;
import com.frangerapp.franger.viewmodel.contact.util.ContactPresentaionConstants;
import com.frangerapp.franger.viewmodel.user.UserBaseViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pavanm on 10/03/18.
 */

public class ContactViewModel extends UserBaseViewModel {


    private LoggedInUser loggedInUser;
    private Context context;
    private EventBus eventBus;
    private ProfileInteractor profileInteractor;

    public ObservableBoolean showLoading = new ObservableBoolean(true);
    private List<ContactListItemViewModel> itemViewModels = new ArrayList<>();
    public ObservableField<List<ContactListItemViewModel>> inviteUserList = new ObservableField<>(itemViewModels);

    public ObservableField<String> searchTxt = new ObservableField<>("");

    public ContactViewModel(Context context, EventBus eventBus, LoggedInUser loggedInUser, ProfileInteractor profileInteractor) {
        this.context = context;
        this.eventBus = eventBus;
        this.profileInteractor = profileInteractor;
        this.loggedInUser = loggedInUser;
        showLoading.set(true);
    }


    private void syncContacts() {
        if (itemViewModels.size() == 0)
            showLoading.set(true);
//        profileInteractor.clearUsersList();

        /**
         * TODO Move the logic of loading from db or api to interactor layer
         * https://proandroiddev.com/the-missing-google-sample-of-android-architecture-components-guide-c7d6e7306b8f
         */
        Disposable disposable = profileInteractor.syncContacts(loggedInUser.getUserId())
                .retry(2)
                .compose(SchedulerUtils.ioToMainObservableScheduler())
                .subscribe(this::onSuccess, this::onFailure, this::onContactsFetchCompleted);
        disposables.add(disposable);

    }

    private void onSuccess(List<Joined> joinedList) {
//        FRLogger.msg("onSuccess " + joinedList);
        //TODO update the progress in ui
    }

    private void onContactsFetchCompleted() {
        FRLogger.msg("onContactsFetchCompleted");
        itemViewModels = new ArrayList<>();

        Disposable disposable = profileInteractor.getSortedUsersList()
                .toObservable()
                .concatMapIterable(user -> user)
                .concatMap(user -> Observable.just(new ContactListItemViewModel(user)))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPhoneNumberAssociated, this::onAllPhoneNumbersAssociationFailed);
        disposables.add(disposable);
    }


    private void onAllPhoneNumbersAssociationFailed(Throwable throwable) {
        showLoading.set(false);
        //TODO move this to ui
        Toast.makeText(context, R.string.common_error_msg, Toast.LENGTH_SHORT).show();
    }

    private void onPhoneNumberAssociated(List<ContactListItemViewModel> inviteUserListItemViewModels) {
        showLoading.set(false);
        itemViewModels.addAll(inviteUserListItemViewModels);
        if (Objects.requireNonNull(searchTxt.get()).isEmpty())
            inviteUserList.set(itemViewModels);
    }


    private void onFailure(Throwable throwable) {
        FRLogger.msg("onfailure " + throwable.getMessage());
        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public final BaseBindingAdapters.ItemClickHandler<ContactListItemViewModel> itemClickHandler = (item) -> {
        FRLogger.msg("item " + item);
        if (item.getUserId() != null && Long.parseLong(item.getUserId()) != 0) {
            ContactEvent contactEvent = new ContactEvent();
            contactEvent.setId(ContactPresentaionConstants.ON_CONTACT_ITEM_CLCKD);
            contactEvent.setContactObj(item);
            eventBus.post(contactEvent);
        }
    };

    public ChatContact getContactModel(ContactListItemViewModel contactListItemViewModel) {
        return new ChatContact(contactListItemViewModel);
    }

    public void onQueryTextChanged(@Nullable String newText) {
        searchTxt.set(newText);
    }

    private void initSearch() {
        Disposable disposable = FieldUtils.toObservable(searchTxt)
                .debounce(30L, TimeUnit.MILLISECONDS)
                .subscribe(this::filterContacts, t -> FRLogger.msg("Error " + t.getMessage()));
        disposables.add(disposable);
    }

    private void filterContacts(String searchTxt) {
        FRLogger.msg("search Txt " + searchTxt);
        Disposable disposable = Observable.just(itemViewModels)
                .concatMapIterable(list -> list)
                .filter(contact ->
                        searchTxt.isEmpty()
                                || (contact.getDisplayName() != null && contact.getDisplayName().toLowerCase().contains(searchTxt.toLowerCase()))
                                || (contact.getPhoneNumber() != null && contact.getPhoneNumber().contains(searchTxt.toLowerCase())))
                .toList()
                .compose(SchedulerUtils.ioToMainSingleScheduler())
                .subscribe(this::onListFiltered, t -> FRLogger.msg("Error " + t.getMessage()));
        disposables.add(disposable);
    }

    private void onListFiltered(List<ContactListItemViewModel> contactListItemViewModels) {
        if (contactListItemViewModels == null)
            contactListItemViewModels = new ArrayList<>();
        inviteUserList.set(contactListItemViewModels);
    }

    public void handleReadContactsPermission(boolean granted) {
        if (granted) { // Always true pre-M
            syncContacts();
        } else {
            // Oops permission denied
            Toast.makeText(context, "permission Needed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPageLoaded() {
        initSearch();
        onContactsFetchCompleted();
    }


    public static class Factory implements ViewModelProvider.Factory {

        private ProfileInteractor profileInteractor;
        private EventBus eventBus;
        private LoggedInUser loggedInUser;
        private Context context;

        public Factory(Context context, EventBus eventBus, LoggedInUser loggedInUser, ProfileInteractor profileInteractor) {
            this.context = context;
            this.loggedInUser = loggedInUser;
            this.eventBus = eventBus;
            this.profileInteractor = profileInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ContactViewModel.class)) {
                return (T) new ContactViewModel(context, eventBus, loggedInUser, profileInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
