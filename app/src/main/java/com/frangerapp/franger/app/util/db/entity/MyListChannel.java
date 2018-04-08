package com.frangerapp.franger.app.util.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.db.converter.DateConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created by pavanm on 22/03/18.
 */

@Entity
public class MyListChannel {

    @PrimaryKey
    @NotNull
    public String channelName;
    @TypeConverters({DateConverter.class})
    public Date updateAt;
    @TypeConverters({DateConverter.class})
    public Date createdAt;
    public long unreadMsgCount;
    public String otherUserId;
    public boolean isUserMuted;
    public String message;

    public MyListChannel(@NonNull String channelName) {
        this.channelName = channelName;
    }

}

