package com.frangerapp.franger.app.util.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.frangerapp.franger.app.util.db.entity.MyListChannel;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by pavanm on 22/03/18.
 */

@Dao
public interface MyListChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addChannel(MyListChannel myListChannel);

    @Query("select * from MyListChannel")
    Single<List<MyListChannel>> getAllChannels();

    @Query("select * from MyListChannel where channelName = :channelName")
    Single<MyListChannel> getChannel(String channelName);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateChannel(MyListChannel myListChannel);

    @Query("delete from MyListChannel")
    void removeAllChannels();
}