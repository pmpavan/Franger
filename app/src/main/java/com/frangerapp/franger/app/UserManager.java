package com.frangerapp.franger.app;

import android.content.Context;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.data.common.UserStore;

/**
 * Created by Pavan on 24/01/18.
 */

public class UserManager {

    private UserStore userStore;
    private Context context;

    public UserManager(Context context, UserStore userStore) {
        this.context = context;
        this.userStore = userStore;
    }

    // if there is any saved user.
    public boolean isUserAvailable() {
        return userStore.getIfUserVerified(context);
    }

    // if the user is onboarded.
    public boolean isUserOnboarded() {
        return userStore.getIfUserOnboarded(context);
    }

    // if the user details added.
    public boolean isUserProfileAdded() {
        return userStore.getIfUserProfileCollected(context);
    }

    // create user component, providing it saved user details to be shared across the user component.
    public boolean createUserSession() {
        boolean isUserSessionCreated = false;
        if (userStore.getUser() != null) {
            FrangerApp.get(context).createUserComponent(userStore.getUser());
            isUserSessionCreated = true;
        }
        return isUserSessionCreated;
    }

    // check
    public boolean isUserSessionCreated() {
        return FrangerApp.get(context).userComponent() != null;
    }

    public void releaseUserSession() {
        FrangerApp.get(context).releaseUserComponent();
    }
}
