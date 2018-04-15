package com.frangerapp.franger.app.util.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.frangerapp.franger.app.util.db.entity.Message;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by pavanm on 21/03/18.
 */

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addMessage(Message message);

    @Query("select * from message")
    public Single<List<Message>> getAllMessages();

    @Query("select * from message where channelName = :channelName order by id asc")
    public Single<List<Message>> getMessages(String channelName);

    @Query("select * from message where id = :id")
    public Single<Message> getMessage(long id);

    @Query("select * from message where channelName = :channelName order by sentAt desc limit 1")
    public Single<Message> getLastMessage(String channelName);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMessage(Message message);

    @Query("delete from message where channelName = :channelName")
    void removeChannelMessages(String channelName);

    @Query("delete from message")
    void removeAllMessages();

}

