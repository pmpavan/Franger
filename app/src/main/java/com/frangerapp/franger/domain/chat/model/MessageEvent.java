package com.frangerapp.franger.domain.chat.model;

/**
 * Created by pavanm on 19/03/18.
 */

public class MessageEvent {


    private int eventType;

    private String userId;

    private String message;

    private String channel;

    private boolean isIncoming;

    public MessageEvent() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "eventType=" + eventType +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", channel='" + channel + '\'' +
                ", isIncoming=" + isIncoming +
                '}';
    }
}
