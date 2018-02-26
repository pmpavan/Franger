package com.frangerapp.franger.domain.profile.interactor.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.contacts.Contact;
import com.frangerapp.contacts.RxContacts;
import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.profile.ProfileApi;
import com.frangerapp.franger.domain.login.exception.LoginFailedException;
import com.frangerapp.franger.domain.login.interactor.impl.LoginPresenterImpl;
import com.frangerapp.franger.domain.profile.exception.ProfileUpdateFailed;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.viewmodel.login.util.LoginValidator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Pavan on 08/02/18.
 */

public class ProfilePresentationImpl implements ProfileInteractor {

    private static final String TAG = LoginPresenterImpl.class.getName();

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
    public Completable syncContacts(@NonNull String userId) {
        HashMap<String, Contact> hashMap = new HashMap<>();
        return RxContacts.fetch(context)
                .buffer(50)
                .flatMap(lists -> {
                    for (Contact contact : lists) {
                        if (contact.getPhoneNumbers() != null || !contact.getPhoneNumbers().isEmpty())
                            for (String phoneNumber : contact.getPhoneNumbers())
                                hashMap.put(phoneNumber, contact);
                    }
                    return profileApi.syncContacts(userId, lists, false)
                            .map(listViewModels -> {
                                //update database
//                            recentCallsListViewModel.setPhoneNumberLookupList(listViewModels);
                                FRLogger.msg("list models " + listViewModels);
                                return listViewModels;
                            }).toObservable();
                })
                .toList()
                .toCompletable();
    }
}
