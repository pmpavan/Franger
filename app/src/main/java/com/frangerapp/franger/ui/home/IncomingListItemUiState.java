package com.frangerapp.franger.ui.home;

import com.frangerapp.franger.app.util.db.entity.User;

import java.util.Date;
import java.util.Objects;

public class IncomingListItemUiState {

    public interface IncomingGroupItemClickHandler {
        void onItemClick(IncomingListItemUiState model);
    }

    public String lastMessage;
    public String userId;
    public String imageUrl;
    public User user;
    public long unreadCount = 0;
    public Date timeStamp;
    public IncomingGroupItemClickHandler handler;
    public long messageId;
    public String channelName;
    public boolean isUserMuted = false;
    public String anonymisedUserName;
    public int anonymisedUserImg;
    public boolean isUserBlocked = false;

    public IncomingListItemUiState() {
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

    public IncomingGroupItemClickHandler getHandler() {
        return handler;
    }

    public void setHandler(IncomingGroupItemClickHandler handler) {
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

    public void onItemClick() {
        if (handler != null) {
            handler.onItemClick(this);
        }
    }

    public String getAnonymisedUserName() {
        return anonymisedUserName;
    }

    public void setAnonymisedUserName(String anonymisedUserName) {
        this.anonymisedUserName = anonymisedUserName;
    }

    public int getAnonymisedUserImg() {
        return anonymisedUserImg;
    }

    public void setAnonymisedUserImg(int anonymisedUserImg) {
        this.anonymisedUserImg = anonymisedUserImg;
    }

    public boolean isUserBlocked() {
        return isUserBlocked;
    }

    public void setUserBlocked(boolean userBlocked) {
        isUserBlocked = userBlocked;
    }

    @Override
    public String toString() {
        return "IncomingListItemUiState{" +
                "lastMessage='" + lastMessage + '\'' +
                ", userId='" + userId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", user=" + user +
                ", unreadCount=" + unreadCount +
                ", timeStamp=" + timeStamp +
                ", handler=" + handler +
                ", messageId=" + messageId +
                ", channelName='" + channelName + '\'' +
                ", isUserMuted=" + isUserMuted +
                ", anonymisedUserName='" + anonymisedUserName + '\'' +
                ", anonymisedUserImg=" + anonymisedUserImg +
                ", isUserBlocked=" + isUserBlocked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncomingListItemUiState that = (IncomingListItemUiState) o;
        return unreadCount == that.unreadCount &&
                messageId == that.messageId &&
                isUserMuted == that.isUserMuted &&
                anonymisedUserImg == that.anonymisedUserImg &&
                isUserBlocked == that.isUserBlocked &&
                Objects.equals(lastMessage, that.lastMessage) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(user, that.user) &&
                Objects.equals(timeStamp, that.timeStamp) &&
                Objects.equals(channelName, that.channelName) &&
                Objects.equals(anonymisedUserName, that.anonymisedUserName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(lastMessage, userId, imageUrl, user, unreadCount, timeStamp, messageId, channelName, isUserMuted, anonymisedUserName, anonymisedUserImg, isUserBlocked);
    }
}
