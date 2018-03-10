package com.frangerapp.franger.viewmodel.invite;

import android.databinding.ObservableBoolean;

import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.app.util.db.entity.User;
import com.frangerapp.franger.viewmodel.BaseViewModel;

/**
 * Created by pavanm on 09/03/18.
 */

public class InviteUserListItemViewModel extends BaseViewModel {
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        setIsExistingUser();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String phoneNumber;
    public String userId = "";
    public String displayName;

    public ObservableBoolean isExistingUser = new ObservableBoolean(false);

    InviteUserListItemViewModel(User user) {
        if (user != null) {
            this.phoneNumber = user.phoneNumber;
            this.userId = user.userId;
            setIsExistingUser();
            this.displayName = user.displayName;
        }
    }

    private void setIsExistingUser() {
        if (userId != null) {
            isExistingUser.set(true);
        } else {
            isExistingUser.set(false);
        }
    }

    public void onInviteButtonClicked() {
        FRLogger.msg("invite btn clicked " + phoneNumber);
    }

    @Override
    public String toString() {
        return "InviteUserListItemViewModel{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
