package com.frangerapp.franger.viewmodel.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.frangerapp.franger.app.util.db.entity.User;

import java.util.Date;

/**
 * Created by pavanm on 30/03/18.
 */

public class ChatListItemUiState implements Parcelable {


    public interface ChatItemClickHandler {
        void onItemClick(int position, ChatListItemUiState model);
    }

    private String message;
    private String userId;
    private User user;
    private Date timeStamp;
    private ChatItemClickHandler handler;
    private long messageId;

    public ChatListItemUiState() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
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

    public ChatItemClickHandler getHandler() {
        return handler;
    }

    public void setHandler(ChatItemClickHandler handler) {
        this.handler = handler;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void onViewClicked() {

    }

    @Override
    public String toString() {
        return "ChatListItemUiState{" +
                "message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", user=" + user +
                ", timeStamp=" + timeStamp +
                ", handler=" + handler +
                ", messageId=" + messageId +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeString(this.userId);
        dest.writeParcelable(this.user, flags);
        dest.writeLong(this.timeStamp != null ? this.timeStamp.getTime() : -1);
        dest.writeLong(this.messageId);
    }

    protected ChatListItemUiState(Parcel in) {
        this.message = in.readString();
        this.userId = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        long tmpTimeStamp = in.readLong();
        this.timeStamp = tmpTimeStamp == -1 ? null : new Date(tmpTimeStamp);
        this.messageId = in.readLong();
    }

    public static final Creator<ChatListItemUiState> CREATOR = new Creator<ChatListItemUiState>() {
        @Override
        public ChatListItemUiState createFromParcel(Parcel source) {
            return new ChatListItemUiState(source);
        }

        @Override
        public ChatListItemUiState[] newArray(int size) {
            return new ChatListItemUiState[size];
        }
    };
}
