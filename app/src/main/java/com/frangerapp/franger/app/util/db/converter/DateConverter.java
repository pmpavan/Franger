package com.frangerapp.franger.app.util.db.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by pavanm on 22/02/18.
 */

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
