package com.frangerapp.franger.domain.chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pavanm on 29/03/18.
 */

public class MessageResponse {

    private long messageId;

    @SerializedName("data")
    @Expose
    private MessageResponseData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("channel")
    @Expose
    private String channel;

    /**
     * No args constructor for use in serialization
     *
     */
    public MessageResponse() {
    }

    /**
     *
     * @param message
     * @param data
     * @param channel
     */
    public MessageResponse(MessageResponseData data, String message, String channel) {
        super();
        this.data = data;
        this.message = message;
        this.channel = channel;
    }

    public MessageResponseData getData() {
        return data;
    }

    public void setData(MessageResponseData data) {
        this.data = data;
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


    @Override
    public String toString() {
        return "MessageResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}
