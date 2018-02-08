package com.frangerapp.franger.app.util.di.module.user.profile;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.domain.profile.interactor.ProfileInteractor;
import com.frangerapp.franger.domain.user.model.User;
import com.frangerapp.franger.ui.profile.AddEditProfileActivity;
import com.frangerapp.franger.viewmodel.profile.ProfileViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 07/02/18.
 */
@Module
public class ProfileModule {

    private boolean isAddPage;

    public ProfileModule(@NonNull AddEditProfileActivity activity, boolean isAddPage) {
        this.isAddPage = isAddPage;
    }

//    @ActivityScope
//    @Provides
//    ProfileViewModel profilePresenter(@NonNull Context context, @NonNull User user, @NonNull UserStore userStore, @NotNull EventBus eventBus, @NotNull ProfileInteractor profileInteractor) {
//        return new ProfileViewModel(context, user, userStore, eventBus, profileInteractor);
//    }

    @Provides
    @ActivityScope
    ViewModelProvider.Factory profileActivityViewModel(@NonNull Context context, @NonNull User user, @NonNull UserStore userStore, @NotNull EventBus eventBus, @NotNull ProfileInteractor profileInteractor) {
        return new ProfileViewModel.Factory(context, user, eventBus, userStore, profileInteractor);
    }
}
