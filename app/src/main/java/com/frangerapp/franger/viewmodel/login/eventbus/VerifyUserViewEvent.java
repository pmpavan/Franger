package com.frangerapp.franger.viewmodel.login.eventbus;

import com.frangerapp.franger.viewmodel.BaseEvent;

/**
 * Created by Pavan on 04/02/18.
 */

public class VerifyUserViewEvent extends BaseEvent {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
