package com.frangerapp.franger.domain.profile.interactor.impl;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.contacts.Contact;
import com.frangerapp.contacts.PhoneNumber;
import com.frangerapp.contacts.RxContacts;
import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.ProfileApi;
import com.frangerapp.franger.data.profile.model.ContactSyncResponse;
import com.frangerapp.franger.data.profile.model.Joined;
import com.frangerapp.franger.domain.login.interactor.impl.LoginPresenterImpl;
import com.frangerapp.franger.domain.profile.exception.ProfileUpdateFailed;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Pavan on 08/02/18.
 */

public class ProfilePresentationImpl implements ProfileInteractor {

    private static final String TAG = ProfilePresentationImpl.class.getName();

    private Context context;
    private ProfileApi profileApi;
    private UserStore userStore;
    private AppDatabase appDatabase;

    public ProfilePresentationImpl(@NonNull Context context, @NonNull ProfileApi profileApi, UserStore userStore, AppDatabase appDatabase) {
        this.context = context;
        this.profileApi = profileApi;
        this.userStore = userStore;
        this.appDatabase = appDatabase;

    }

    @Override
    public Completable submitProfile(@NotNull String userId, @NotNull String userName) {
        return profileApi.editProfile(userId, userName).flatMap(loginDetail -> Single.create(s -> {
            if (loginDetail.getUpdated() != null && loginDetail.getUpdated().contains("name")) {
                userStore.setUserProfileCollected(context, true);
                userStore.storeUserName(context, userName);
                FRLogger.msg("profile updated " + loginDetail);
                s.onSuccess(loginDetail);
            } else {
                s.onError(new ProfileUpdateFailed());
            }
        })).toCompletable();
    }

    @Override
    public Observable<ContactSyncResponse> syncContacts(@NonNull String userId) {
        return RxContacts.fetch(context)
                .buffer(50)
                .concatMap(lists -> {
                    addUserToDb(lists);
                    Single<ContactSyncResponse> contactSyncResponseSingle = profileApi.syncContacts(userId, lists, false)
                            .map(listViewModels -> {
                                FRLogger.msg("list models 1" + listViewModels);
                                //update database
                                updateUserTableWithId(listViewModels)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(this::onPhoneNumberAssociated, this::onAllPhoneNumbersAssociationFailed);
                                return listViewModels;
                            });
                    return contactSyncResponseSingle.toObservable();
                });
    }

    private void addUserToDb(List<Contact> lists) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        for (Contact contact : lists) {
            if (contact.getPhoneNumbers() != null || !contact.getPhoneNumbers().isEmpty())
                for (PhoneNumber phoneNumber : contact.getPhoneNumbers()) {
                    User user = new User();
                    user.phoneNumber = phoneNumber.getPhoneNumber();
                    user.displayName = contact.getDisplayName();
                    String phoneNum = phoneNumber.getPhoneNumber();
                    try {
                        Phonenumber.PhoneNumber cleanedPhoneNumber = phoneUtil.parse(phoneNum, "IN");
                        if (cleanedPhoneNumber.hasNationalNumber()) {
                            phoneNum = String.valueOf(cleanedPhoneNumber.getNationalNumber());
                        }
                    } catch (NumberParseException ignored) {
                    }
                    user.cleanedPhoneNumber = phoneNum;
//                                user.phoneNumberType = phoneNumber.getPhoneType();
                    appDatabase.userDao().addUser(user);
                }
        }
    }

    private Observable<Joined> updateUserTableWithId(ContactSyncResponse listViewModels) {
        return Observable.just(listViewModels)
                .flatMapIterable(contactSyncResponse -> {
                    FRLogger.msg("list models 1 response " + contactSyncResponse.getJoinedList());
                    return contactSyncResponse.getJoinedList();
                })
                .flatMap(Observable::just);
    }

    private void onAllPhoneNumbersAssociationFailed(Throwable throwable) {

    }

    private void onPhoneNumberAssociated(Joined joined) {
        User user = new User();
        user.phoneNumber = joined.getOriginalNumber();
        user.userId = joined.getUserId();
        appDatabase.userDao().updateUser(joined.getUserId(), joined.getOriginalNumber());
    }

    @Override
    public Single<List<User>> getExistingUsersList() {
        return appDatabase.userDao().getExistingUsers();
    }

    @Override
    public Single<List<User>> getNonFrangerUsersList() {
        return appDatabase.userDao().getNonExistingUsers();
    }

    @Override
    public Observable<List<User>> getSortedUsersList() {
        return Observable.zip(getExistingUsersList().toObservable(), getNonFrangerUsersList().toObservable(),
                (users, users2) -> {
                    List<com.frangerapp.franger.app.util.db.entity.User> list = new ArrayList<>(users);
                    list.addAll(users2);
                    return list;
                });
    }

    @Override
    public void clearUsersList() {
        appDatabase.userDao().removeAllUsers();
    }

    @Override
    public void setUserInviteCompleted() {
        userStore.setUserOnboarded(context, true);
    }
}
