package com.frangerapp.franger.domain.chat.model;

import com.frangerapp.franger.app.util.db.entity.User;

import java.util.Date;

/**
 * Created by pavanm on 19/03/18.
 */

public class MessageEvent {


    private int eventType;

    private String userId;

    private String message;

    private String channel;

    private boolean isIncoming;

    private Date timestamp;

    private User user;

    private long messageId;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "eventType=" + eventType +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", channel='" + channel + '\'' +
                ", isIncoming=" + isIncoming +
                ", timestamp=" + timestamp +
                ", user=" + user +
                ", messageId=" + messageId +
                '}';
    }
}
