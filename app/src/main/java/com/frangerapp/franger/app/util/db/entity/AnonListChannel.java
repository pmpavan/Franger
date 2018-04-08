package com.frangerapp.franger.app.util.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created by pavanm on 27/03/18.
 */

@Entity
public class AnonListChannel {

    @PrimaryKey
    @NotNull
    public String channelName;
    public Date updateAt;
    public Date createdAt;
    public long unreadMsgCount;
    public String otherUserId;
    public String anonymisedUserName;
    public int anonymisedUserImg;
    public boolean isUserBlocked = false;
    public String message;

    public AnonListChannel(@NonNull String channelName) {
        this.channelName = channelName;
    }
}
