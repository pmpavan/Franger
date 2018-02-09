package com.frangerapp.franger.domain.profile.interactor;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;

/**
 * Created by Pavan on 08/02/18.
 */

public interface ProfileInteractor {

    Completable submitProfile(@NotNull String userId, @NotNull String userName);
}
