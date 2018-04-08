package com.frangerapp.franger.ui.home;

import com.frangerapp.franger.app.util.db.entity.User;

import java.util.Date;

public class OutgoingListItemUiState {

    public interface OutgoingGroupItemClickHandler {
        void onItemClick( OutgoingListItemUiState model);
    }

    public String lastMessage;
    public String userId;
    public String imageUrl;
    public User user;
    public int unreadCount = 0;
    public Date timeStamp;
    public OutgoingGroupItemClickHandler handler;
    public long messageId;
    public String channelName;
    public boolean isUserMuted = false;

    public OutgoingListItemUiState() {
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public OutgoingGroupItemClickHandler getHandler() {
        return handler;
    }

    public void setHandler(OutgoingGroupItemClickHandler handler) {
        this.handler = handler;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isUserMuted() {
        return isUserMuted;
    }

    public void setUserMuted(boolean userMuted) {
        isUserMuted = userMuted;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void onItemClick(){
        if(handler!=null){
            handler.onItemClick(this);
        }
    }
    @Override
    public String toString() {
        return "OutgoingListItemUiState{" +
                "lastMessage='" + lastMessage + '\'' +
                ", userId='" + userId + '\'' +
                ", user=" + user +
                ", unreadCount=" + unreadCount +
                ", timeStamp=" + timeStamp +
                ", handler=" + handler +
                ", messageId=" + messageId +
                ", channelName='" + channelName + '\'' +
                ", isUserMuted=" + isUserMuted +
                '}';
    }
}
