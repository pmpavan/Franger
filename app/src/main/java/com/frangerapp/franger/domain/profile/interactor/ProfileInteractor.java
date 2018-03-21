package com.frangerapp.franger.domain.profile.interactor;

import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.data.profile.model.Joined;

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

    Observable<List<Joined>> syncContacts(@NonNull final String userId);

    Single<List<User>> getFrangerUsersList();

    Single<List<User>> getNonFrangerUsersList();

    Observable<List<User>> getSortedUsersList();

    void clearUsersList();

    void setUserInviteCompleted();
}
