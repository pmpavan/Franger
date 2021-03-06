package com.frangerapp.franger.ui.user;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.frangerapp.franger.app.FrangerApp;
import com.frangerapp.franger.app.UserManager;
import com.frangerapp.franger.ui.BaseActivity;

import javax.inject.Inject;

/**
 * Created by Pavan on 24/01/18.
 */

public abstract class UserBaseActivity extends BaseActivity {


    @Inject
    UserManager userManager;

    // FRAGMENT METHODS.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrangerApp.get(this).appComponent().inject(this);
        createUserComponent();
    }

    // PRIVATE METHODS.
    private void createUserComponent() {
        if (!userManager.isUserSessionCreated()) {
            boolean isUserSessionCreated = userManager.createUserSession();

            // If userSession not created then logOut user.
            if (!isUserSessionCreated) {
                logoutUser();
            }
        }
    }

    private void logoutUser() {
        // Release UserComponent.
        userManager.releaseUserSession();
    }
}
