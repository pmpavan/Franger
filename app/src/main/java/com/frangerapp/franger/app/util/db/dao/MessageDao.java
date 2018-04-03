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

    @Query("select * from message where userId = :userId")
    public Single<List<Message>> getMessages(String userId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMessage(Message message);

    @Query("delete from message")
    void removeAllMessages();

}

