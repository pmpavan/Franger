package com.frangerapp.franger.domain.chat.model;

/**
 * Created by pavanm on 16/03/18.
 */

public class ChatMessage {

    private String channel;
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
}
