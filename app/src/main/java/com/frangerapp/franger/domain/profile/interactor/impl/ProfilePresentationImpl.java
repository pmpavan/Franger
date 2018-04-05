package com.frangerapp.franger.domain.profile.interactor.impl;

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
import com.frangerapp.franger.domain.profile.exception.ProfileUpdateFailed;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

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

//    private Single<ContactSyncResponse> updateUserInDb(ContactSyncResponse response) {
//
//        return Single.fromCallable(() -> {
//            Observable.just(response)
//                    .flatMapIterable(contactSyncResponse -> {
//                        FRLogger.msg("list models 1 response " + contactSyncResponse.getJoinedList());
//                        return contactSyncResponse;
//                    })
//        });
//    }

    @Override
    public Observable<List<Joined>> syncContacts(@NonNull String userId) {
        return RxContacts.fetch(context)
//                .map(contact -> {
//                    clearUsersList();
//                    return contact;
//                })
                .buffer(50)
                .flatMapSingle(this::addUserToDb)
                .flatMapSingle(lists -> profileApi.syncContacts(userId, lists, false))
                .map(ContactSyncResponse::getJoinedList)
                .flatMapIterable(joineds -> {
                    FRLogger.msg("joined   " + joineds);
                    return joineds;
                })
                .flatMapSingle(joined -> Single.fromCallable(() -> {
                    User user = new User();
                    user.phoneNumber = joined.getOriginalNumber();
                    user.userId = joined.getUserId();
                    appDatabase.userDao().updateUser(joined.getUserId(), joined.getOriginalNumber());
                    FRLogger.msg("user   " + user);
                    return joined;
                }))
                .toList()
                .toObservable();


//                .concatMap(lists -> {
//                    addUserToDb(lists);
//                    Single<ContactSyncResponse> contactSyncResponseSingle = profileApi.syncContacts(userId, lists, false)
//                            .map(listViewModels -> {
//                                FRLogger.msg("list models 1" + listViewModels);
//                                //update database
//                                updateUserTableWithId(listViewModels);
//                                return listViewModels;
//                            });
//                    return contactSyncResponseSingle.toObservable();
//                });
    }

    private Single<List<Contact>> addUserToDb(List<Contact> lists) {
        return Single.fromCallable(() -> {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            ArrayList<User> users = new ArrayList<>();
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
//                    user.phoneNumberType = phoneNumber.getPhoneType();
                        users.add(user);
                    }
            }
            if (users.size() > 0)
                appDatabase.userDao().addUser(users);
            return lists;
        });
    }

    private Observable<Joined> updateUserTableWithId(ContactSyncResponse listViewModels) {
        return Observable.just(listViewModels)
                .flatMapIterable(contactSyncResponse -> {
                    FRLogger.msg("list models 1 response " + contactSyncResponse.getJoinedList());
                    return contactSyncResponse.getJoinedList();
                })
                .flatMap(Observable::just);
    }

    @Override
    public Single<List<User>> getFrangerUsersList() {
        return appDatabase.userDao().getExistingUsers();
    }

    @Override
    public Single<List<User>> getNonFrangerUsersList() {
        return appDatabase.userDao().getNonExistingUsers();
    }

    @Override
    public Single<List<User>> getSortedUsersList() {
//        MediatorLiveData mediatorLiveData = new MediatorLiveData<LoggedInUser>();
//        mediatorLiveData.addSource(getFrangerUsersList(),value -> {
//            mediatorLiveData.setValue(value);
//        });
//        mediatorLiveData.addSource(getNonFrangerUsersList(),value -> {
//            mediatorLiveData.setValue(value);
//        });

        return Single.zip(getFrangerUsersList(), getNonFrangerUsersList(),
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
