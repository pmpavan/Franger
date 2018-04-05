package com.frangerapp.franger.app.util.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.frangerapp.franger.app.util.db.converter.DateConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created by pavanm on 21/03/18.
 */

@Entity
public class Message {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    //    @ForeignKey(entity = LoggedInUser.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE)
    @NotNull
    public String userId;
    @NotNull
    public String channelName;
    @NotNull
    public String message;
    @TypeConverters(DateConverter.class)
    public Date sentAt;
    public int messageStatus;
}
