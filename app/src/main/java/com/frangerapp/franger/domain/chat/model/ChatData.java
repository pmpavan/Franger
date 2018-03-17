package com.frangerapp.franger.domain.chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavan on 15/01/18.
 */

public class ChatData {

    @SerializedName("fromId")
    @Expose
    private String fromId;
    @SerializedName("toId")
    @Expose
    private String toId;
    @SerializedName("userId")
    @Expose
    private String userId;

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    @Override
    public String toString() {
        return "ChatData{" +
                "fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
