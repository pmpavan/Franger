package com.frangerapp.franger.ui.home;

import com.frangerapp.franger.app.util.db.entity.User;

import java.util.Date;
import java.util.Objects;

public class OutgoingListItemUiState {

    public OutgoingGroupItemClickHandler getHandler() {
        return handler;
    }

    public void setHandler(OutgoingGroupItemClickHandler handler) {
        this.handler = handler;
    }

    public interface OutgoingGroupItemClickHandler {
        void onItemClick(OutgoingListItemUiState item);
    }

    public String lastMessage;
    public String userId;
    public String imageUrl;
    public User user;
    public long unreadCount = 0;

    private OutgoingGroupItemClickHandler handler;
    public Date timeStamp;
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

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
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

    @Override
    public String toString() {
        return "OutgoingListItemUiState{" +
                "lastMessage='" + lastMessage + '\'' +
                ", userId='" + userId + '\'' +
                ", user=" + user +
                ", unreadCount=" + unreadCount +
                ", timeStamp=" + timeStamp +
                ", messageId=" + messageId +
                ", channelName='" + channelName + '\'' +
                ", isUserMuted=" + isUserMuted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutgoingListItemUiState that = (OutgoingListItemUiState) o;
        return unreadCount == that.unreadCount &&
                messageId == that.messageId &&
                isUserMuted == that.isUserMuted &&
                Objects.equals(lastMessage, that.lastMessage) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(user, that.user) &&
                Objects.equals(timeStamp, that.timeStamp) &&
                Objects.equals(channelName, that.channelName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(lastMessage, userId, imageUrl, user, unreadCount, timeStamp, messageId, channelName, isUserMuted);
    }
}
