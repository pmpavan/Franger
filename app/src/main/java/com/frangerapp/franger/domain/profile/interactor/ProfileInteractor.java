package com.frangerapp.franger.domain.profile.interactor;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.data.profile.model.ContactSyncResponse;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Pavan on 08/02/18.
 */

public interface ProfileInteractor {

    Completable submitProfile(@NotNull String userId, @NotNull String userName);

    Observable<ContactSyncResponse> syncContacts(@NonNull final String userId);

    Single<List<User>> getExistingUsersList();

    Single<List<User>> getNonFrangerUsersList();

    Observable<List<User>> getSortedUsersList();

    void clearUsersList();

    void setUserInviteCompleted();
}
