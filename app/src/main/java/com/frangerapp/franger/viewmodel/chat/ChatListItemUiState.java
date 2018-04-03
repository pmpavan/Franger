package com.frangerapp.franger.viewmodel.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.frangerapp.franger.app.util.db.entity.User;

import java.util.Date;

/**
 * Created by pavanm on 30/03/18.
 */

public class ChatListItemUiState implements Parcelable {


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

    public interface ChatItemClickHandler {
        void onItemClick(int position, ChatListItemUiState model);
    }

    public enum CHAT_ITEM_TYPE {
        INCOMING(1),
        OUTGOING(2);
        public int type;

        CHAT_ITEM_TYPE(int type) {
            this.type = type;
        }
    }

    private CHAT_ITEM_TYPE type;
    private String message;
    private String userId;
    private User user;
    private Date timeStamp;
    private ChatItemClickHandler handler;
    private long messageId;
    public ChatListItemUiState() {
    }

    public ChatItemClickHandler getHandler() {
        return handler;
    }

    public void setHandler(ChatItemClickHandler handler) {
        this.handler = handler;
    }

    public CHAT_ITEM_TYPE getType() {
        return type;
    }


    public void setType(CHAT_ITEM_TYPE type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "ChatListItemUiState{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.message);
        dest.writeString(this.userId);
        dest.writeParcelable(this.user, flags);
        dest.writeLong(this.timeStamp != null ? this.timeStamp.getTime() : -1);
        dest.writeLong(this.messageId);
    }

    protected ChatListItemUiState(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : CHAT_ITEM_TYPE.values()[tmpType];
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
