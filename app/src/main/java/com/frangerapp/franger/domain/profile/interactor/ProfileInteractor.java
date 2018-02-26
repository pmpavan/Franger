package com.frangerapp.franger.domain.profile.interactor;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Pavan on 08/02/18.
 */

public interface ProfileInteractor {

    Completable submitProfile(@NotNull String userId, @NotNull String userName);

    Completable syncContacts(@NonNull final String userId);
}
