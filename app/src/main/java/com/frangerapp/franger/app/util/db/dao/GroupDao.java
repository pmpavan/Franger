package com.frangerapp.franger.app.util.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.frangerapp.franger.app.util.db.entity.Group;

import java.util.List;

/**
 * Created by pavanm on 22/03/18.
 */

@Dao
public interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addGroup(Group group);

    @Query("select * from `group`")
    public LiveData<List<Group>> getAllGroups();

    @Query("select * from `group` where isIncoming = `false`")
    public LiveData<List<Group>> getOutgoingGroups();

    @Query("select * from `group` where isIncoming = `true`")
    public LiveData<List<Group>> getIncomingGroups();

    @Query("select * from `group` where isIncoming = :isIncoming")
    public LiveData<List<Group>> getGroups(boolean isIncoming);

    @Query("select * from `group` where channelName = :groupId")
    public LiveData<List<Group>> getGroups(String groupId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateGroup(Group group);

    @Query("delete from `group`")
    void removeAllGroups();
}