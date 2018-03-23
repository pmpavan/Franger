package com.frangerapp.franger.app.util.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.frangerapp.franger.app.util.db.entity.Channel;

import java.util.List;

/**
 * Created by pavanm on 22/03/18.
 */

@Dao
public interface ChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addChannel(Channel channel);

    @Query("select * from Channel")
    public LiveData<List<Channel>> getAllChannels();

    @Query("select * from Channel where isIncoming = `false`")
    public LiveData<List<Channel>> getOutgoingChannels();

    @Query("select * from Channel where isIncoming = `true`")
    public LiveData<List<Channel>> getIncomingChannels();

    @Query("select * from Channel where isIncoming = :isIncoming")
    public LiveData<List<Channel>> getChannels(boolean isIncoming);

    @Query("select * from Channel where channelName = :groupId")
    public LiveData<List<Channel>> getChannels(String groupId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateChannel(Channel channel);

    @Query("delete from Channel")
    void removeAllChannels();
}