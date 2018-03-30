package com.frangerapp.franger.domain.chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pavanm on 29/03/18.
 */

public class MessageResponseData {

    @SerializedName("fromId")
    @Expose
    private String fromId;

    /**
     * No args constructor for use in serialization
     */
    public MessageResponseData() {
    }

    /**
     * @param fromId
     */
    public MessageResponseData(String fromId) {
        super();
        this.fromId = fromId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }


    @Override
    public String toString() {
        return "MessageResponseData{" +
                "fromId='" + fromId + '\'' +
                '}';
    }
}
