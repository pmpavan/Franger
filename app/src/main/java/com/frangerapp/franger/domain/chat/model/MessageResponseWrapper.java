package com.frangerapp.franger.domain.chat.model;

import com.frangerapp.franger.data.chat.model.AnonymousUser;

public class MessageResponseWrapper {

    private long messageId;
    private String fromId;
    private String message;
    private String channel;
    private AnonymousUser anonymousUser;

    public MessageResponseWrapper() {
    }

    public static MessageResponseWrapper builder() {
        return new MessageResponseWrapper();
    }

    public long getMessageId() {
        return messageId;
    }

    public MessageResponseWrapper setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getFromId() {
        return fromId;
    }

    public MessageResponseWrapper setFromId(String fromId) {
        this.fromId = fromId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MessageResponseWrapper setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public MessageResponseWrapper setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public AnonymousUser getAnonymousUser() {
        return anonymousUser;
    }

    public void setAnonymousUser(AnonymousUser anonymousUser) {
        this.anonymousUser = anonymousUser;
    }

    @Override
    public String toString() {
        return "MessageResponseWrapper{" +
                "messageId=" + messageId +
                ", fromId='" + fromId + '\'' +
                ", message='" + message + '\'' +
                ", channel='" + channel + '\'' +
                ", anonymousUser=" + anonymousUser +
                '}';
    }
}
