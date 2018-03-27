package com.frangerapp.franger.app.util.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.frangerapp.franger.app.util.db.entity.AnonListChannel;

import java.util.List;

/**
 * Created by pavanm on 27/03/18.
 */
@Dao
public interface AnonListDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addChannel(AnonListChannel anonListChannel);

    @Query("select * from AnonListChannel")
     LiveData<List<AnonListChannel>> getAllChannels();

    @Query("select * from AnonListChannel where channelName = :groupId")
    LiveData<List<AnonListChannel>> getChannels(String groupId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateChannel(AnonListChannel anonListChannel);

    @Query("delete from AnonListChannel")
    void removeAllChannels();
}
