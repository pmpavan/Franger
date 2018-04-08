package com.frangerapp.franger.domain.chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavan on 10/01/18.
 */

public class FeedNewMessageResponse {

    @SerializedName("messageFrom")
    @Expose
    private MessageFrom messageFrom;
    @SerializedName("channel")
    @Expose
    private String channel;
    @SerializedName("message")
    @Expose
    private String message;


    public FeedNewMessageResponse() {

    }

    public MessageFrom getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(MessageFrom messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
