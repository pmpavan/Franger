package com.frangerapp.franger.app.util.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.frangerapp.franger.app.util.db.converter.DateConverter;
import com.frangerapp.franger.domain.user.model.LoggedInUser;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by pavanm on 21/03/18.
 */

@Entity
public class Message {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    //    @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE)
    @NotNull
    public String userId;
    @NotNull
    public String channelName;
    @NotNull
    public String message;
    @TypeConverters(DateConverter.class)
    public Date sentAt;
    public int messageStatus;
    public boolean isMessageRead = false;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", message='" + message + '\'' +
                ", sentAt=" + sentAt +
                ", messageStatus=" + messageStatus +
                ", isMessageRead=" + isMessageRead +
                '}';
    }
}
