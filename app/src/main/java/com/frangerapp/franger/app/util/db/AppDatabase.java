package com.frangerapp.franger.app.util.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.frangerapp.franger.app.util.db.converter.DateConverter;
import com.frangerapp.franger.app.util.db.dao.AnonListDao;
import com.frangerapp.franger.app.util.db.dao.MessageDao;
import com.frangerapp.franger.app.util.db.dao.MyListChannelDao;
import com.frangerapp.franger.app.util.db.dao.UserDao;
import com.frangerapp.franger.app.util.db.entity.AnonListChannel;
import com.frangerapp.franger.app.util.db.entity.MyListChannel;
import com.frangerapp.franger.app.util.db.entity.Message;
import com.frangerapp.franger.app.util.db.entity.User;

/**
 * Created by pavanm on 22/02/18.
 */
@Database(entities = {User.class, Message.class, MyListChannel.class, AnonListChannel.class},
        version = 3, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract MessageDao messageDao();

    public abstract MyListChannelDao myListChannelDao();

    public abstract AnonListDao anonListDao();


    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "FrangerAppDatabase")
                            //Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                            // To simplify the exercise, allow queries on the main thread.
                            // Don't do this on a real app!
                            .allowMainThreadQueries()
                            // recreate the database if necessary
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


}
