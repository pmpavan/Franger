package com.frangerapp.franger.domain.chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pavanm on 16/03/18.
 */

public class ChatMessage {

    @SerializedName("channel")
    @Expose
    private String channel;
    @SerializedName("message")
    @Expose
    private String message;

    public ChatMessage() {
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channelName) {
        this.channel = channelName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "channel='" + channel + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
