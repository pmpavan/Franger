package com.frangerapp.franger.viewmodel.splash;

import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.splash.eventbus.SplashViewEvent;
import com.frangerapp.franger.viewmodel.splash.util.SplashPresentationConstants;
import com.frangerapp.franger.viewmodel.splash.view.SplashView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Pavan on 20/01/18.
 */

public class SplashViewModel extends BaseViewModel implements SplashView {


    private boolean isUserLoggedIn = false;

    public SplashViewModel() {
        init();
    }

    private void init() {
    }

    @Override
    public void onPageLoaded() {
        checkIfUserIsLoggedInAndMoveToRespectivePage();
    }

    private void checkIfUserIsLoggedInAndMoveToRespectivePage() {
        //TODO Check If the user is logged in or not
        int eventId = SplashPresentationConstants.NAVIGATE_TO_LOGIN_EVENT;
        if (isUserLoggedIn) {
            eventId = SplashPresentationConstants.NAVIGATE_TO_HOME_EVENT;
        }
        SplashViewEvent event = new SplashViewEvent();
        event.setId(eventId);
        EventBus.getDefault().post(event);

    }
}
