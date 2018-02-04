package com.frangerapp.franger.viewmodel.login.eventbus;

import com.frangerapp.franger.viewmodel.BaseEvent;

/**
 * Created by Pavan on 20/01/18.
 */

public class LoginViewEvent extends BaseEvent {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
