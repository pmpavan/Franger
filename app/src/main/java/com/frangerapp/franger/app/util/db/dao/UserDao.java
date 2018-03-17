package com.frangerapp.franger.app.util.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.frangerapp.franger.app.util.db.entity.User;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by pavanm on 22/02/18.
 */

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(User user);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(List<User> users);

    @Query("select * FROM user  where (userId <> 0) ORDER BY displayName ASC")
    Single<List<User>> getExistingUsers();

    @Query("select * FROM user  where (userId = 0) ORDER BY displayName ASC")
    Single<List<User>> getNonExistingUsers();

    @Query("select * from user")
    Single<List<User>> getAllUser();

    @Query("select * from user where userId = :userId")
    LiveData<User> getUser(String userId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(User user);

    @Query("UPDATE User SET userId = :userId  WHERE phoneNumber = :phoneNumber")
    int updateUser(String userId, String phoneNumber);

    @Query("delete from user")
    void removeAllUsers();
}
