package com.frangerapp.franger.viewmodel.splash;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.BuildConfig;
import com.frangerapp.franger.app.UserManager;
import com.frangerapp.franger.domain.splash.interactor.SplashInteractor;
import com.frangerapp.franger.viewmodel.login.LoginBaseViewModel;
import com.frangerapp.franger.viewmodel.splash.eventbus.SplashViewEvent;
import com.frangerapp.franger.viewmodel.splash.util.SplashPresentationConstants;
import com.frangerapp.franger.viewmodel.splash.view.SplashView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Pavan on 20/01/18.
 */

public class SplashViewModel extends LoginBaseViewModel implements SplashView {


    private Context context;
    private EventBus eventBus;
    private UserManager userManager;
    private SplashInteractor splashInteractor;

    public SplashViewModel(Context context, EventBus eventBus, UserManager userManager, SplashInteractor splashInteractor) {
        this.context = context;
        this.userManager = userManager;
        this.eventBus = eventBus;
        this.splashInteractor = splashInteractor;
        init();
    }

    private void init() {
        splashInteractor.setAppVersionCode(BuildConfig.VERSION_CODE);
    }

    @Override
    public void onPageLoaded() {
        checkIfUserIsLoggedInAndMoveToRespectivePage();
    }

    private void checkIfUserIsLoggedInAndMoveToRespectivePage() {
        boolean isUserLoggedIn = userManager.isUserAvailable();
        boolean isUserOnboarded = userManager.isUserOnboarded();
        boolean isUserProfileAdded = userManager.isUserProfileAdded();
        int eventId = SplashPresentationConstants.NAVIGATE_TO_LOGIN_EVENT;
        if (isUserLoggedIn) {
            eventId = SplashPresentationConstants.NAVIGATE_TO_HOME_EVENT;
            if (!isUserProfileAdded) {
                eventId = SplashPresentationConstants.NAVIGATE_TO_USER_PROFILE_EVENT;
            } else if (!isUserOnboarded) {
                eventId = SplashPresentationConstants.NAVIGATE_TO_ONBOARD_EVENT;
            }
        }
        SplashViewEvent event = new SplashViewEvent();
        event.setId(eventId);
        eventBus.post(event);

    }

    public static class Factory implements ViewModelProvider.Factory {

        private EventBus eventBus;
        private Context context;
        private UserManager userManager;
        private SplashInteractor splashInteractor;

        public Factory(Context context, EventBus eventBus, UserManager userManager, SplashInteractor splashInteractor) {
            this.eventBus = eventBus;
            this.context = context;
            this.userManager = userManager;
            this.splashInteractor = splashInteractor;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SplashViewModel.class)) {
                return (T) new SplashViewModel(context, eventBus, userManager, splashInteractor);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }
}
