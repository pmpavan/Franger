package com.frangerapp.franger.viewmodel;

/**
 * Created by Pavan on 20/01/18.
 */

public class BaseEvent {

    protected int id;
    protected String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
